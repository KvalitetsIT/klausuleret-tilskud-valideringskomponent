package dk.kvalitetsit.hello.integrationtest;

import dk.kvalitetsit.hello.Application;
import org.jetbrains.annotations.NotNull;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.KithugsApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Locale;

public class IntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    // Create one API instance for testing
    public static final KithugsApi api = createApi();

    static {
        setLanguage();
    }

    private static void setLanguage() {
        //On certain computers the language for Spring error messages defaults to Danish.
        //This would cause certain tests where we compare error messages to strings to fail.
        //This forces the language of error messages to English, preventing any issues.
        Locale.setDefault(Locale.ENGLISH);
    }

    private static KithugsApi createApi() {
        var runInDocker = Boolean.getBoolean("runInDocker");
        logger.info("Running integration test in docker container: {}", runInDocker);
        var uri = runInDocker ? startDockerCompose() : startServices(); // Debugging is not possible if running in docker
        var apiClient = new ApiClient(); apiClient.setBasePath(uri.toString());
        return new KithugsApi(apiClient);
    }

    private static URI startServices() {
        var jdbcUrl = setupDatabaseContainer();
        System.setProperty("JDBC.URL", jdbcUrl);
        System.setProperty("JDBC.USER", "hellouser");
        System.setProperty("JDBC.PASS", "secret1234");
        SpringApplication.run(Application.class);
        try {
            return new URI("http://localhost:8080");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String setupDatabaseContainer() {
        var mariadb = new MariaDBContainer<>("mariadb:10.6")
                .withDatabaseName("hellodb")
                .withUsername("hellouser")
                .withPassword("secret1234")
                .withNetworkAliases("mariadb");
        mariadb.start();
        attachLogger(LoggerFactory.getLogger("mariadb"), mariadb);
        return mariadb.getJdbcUrl();
    }

    private static void attachLogger(Logger logger, GenericContainer<?> container) {
        IntegrationTest.logger.info("Attaching logger to container: {}", container.getContainerInfo().getName());
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }

    private static URI startDockerCompose() {
        String serviceName = "validation-qa";
        int servicePort = 8080;
        ComposeContainer composeContainer = new ComposeContainer(getComposeFile())
                .withExposedService(serviceName, servicePort)
                .withRemoveVolumes(true)
                .withLogConsumer(serviceName, new Slf4jLogConsumer(logger).withPrefix((serviceName)));
        composeContainer.start();
        String containerHost = composeContainer.getServiceHost(serviceName, servicePort);
        int containerPort = composeContainer.getServicePort(serviceName, servicePort);
        try {
            return new URI("http://" + containerHost + ":" + containerPort);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull File getComposeFile() {
        var testWorkingDir = System.getProperty("user.dir");
        var projectRoot = Paths.get(testWorkingDir).toAbsolutePath().normalize().getParent().toFile();
        return new File(projectRoot, "documentation/docker/compose/docker-compose.yml");
    }
}