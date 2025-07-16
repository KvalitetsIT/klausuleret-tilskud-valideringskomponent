package dk.kvalitetsit.klaus.integrationtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openapitools.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

@SuppressWarnings("resource")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static ApiClient client;
    private static final ComposeContainer environment;
    private static final boolean runInDocker = Boolean.getBoolean("runInDocker");

    @LocalServerPort
    protected int localServerPort;

    static {
        var testWorkingDir = System.getProperty("user.dir");
        var projectRoot = Paths.get(testWorkingDir).toAbsolutePath().normalize().getParent().toFile();
        var composeFile = new File(projectRoot, "compose/development/docker-compose.yml");

        environment = runInDocker ? runInDocker(composeFile) : runOutsideDocker(composeFile);
        environment.start();

        Runtime.getRuntime().addShutdownHook(new Thread(environment::stop));
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        setupAndRegisterProperties("master-db", "master", "sdm_krs_a", "", registry);
        setupAndRegisterProperties("validation-db", "validation", "validation_db", "rootroot", registry);
    }

    @BeforeEach
    void setupApiClient() {
        String host;
        String port;

        if (runInDocker) {
            host = environment.getServiceHost("validation-component", 8080);
            port = environment.getServicePort("validation-component", 8080).toString();
        } else {
            host = "localhost";
            port = String.valueOf(localServerPort);
        }

        client = new ApiClient().setBasePath(String.format("http://%s:%s", host, port));
    }

    private static void setupAndRegisterProperties(
            String serviceName,
            String prefix,
            String db,
            String password,
            DynamicPropertyRegistry registry
    ) {
        String host = environment.getServiceHost(serviceName, 3306);
        Integer port = environment.getServicePort(serviceName, 3306);
        registry.add("app.jdbc." + prefix + ".url", () -> "jdbc:mariadb://" + host + ":" + port + "/" + db);
        registry.add("app.jdbc." + prefix + ".username", () -> "root");
        registry.add("app.jdbc." + prefix + ".password", () -> password);
    }

    private static ComposeContainer runOutsideDocker(File composeFile) {
        return new ComposeContainer(composeFile)
                .withServices("validation-db", "master-db")
                .withExposedService("validation-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withExposedService("master-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLocalCompose(true);
    }

    private static ComposeContainer runInDocker(File composeFile) {
        return new ComposeContainer(composeFile)
                .withServices("validation-db", "master-db", "validation-component")
                .withExposedService("validation-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("validation-db", new Slf4jLogConsumer(logger).withPrefix("validation-db"))
                .withExposedService("master-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("master-db", new Slf4jLogConsumer(logger).withPrefix("master-db"))
                .withExposedService("validation-component", 8080, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("validation-component", new Slf4jLogConsumer(logger).withPrefix("validation-component"))
                .withLocalCompose(false);
    }

}
