package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openapitools.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@SuppressWarnings("resource")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    private static final boolean runInDocker = Boolean.getBoolean("runInDocker");

    private static final ComposeContainer environment = createEnvironment();
    protected static ApiClient client;
    @LocalServerPort
    protected int localServerPort;

    private static ComposeContainer createEnvironment() {
        var testWorkingDir = System.getProperty("user.dir");
        var projectRoot = Paths.get(testWorkingDir).toAbsolutePath().normalize().getParent().toFile();
        var composeFile = new File(projectRoot, "compose/development/docker-compose.yml");

        return runInDocker ? runInDocker(composeFile) : runOutsideDocker(composeFile);
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        environment.start();
        setupAndRegisterProperties("stamdata-db", "validation.stamdata", "sdm_krs_a", "", registry);
        setupAndRegisterProperties("app-db", "common", "validation_db", "rootroot", registry);
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
        registry.add("itukt." + prefix + ".jdbc.url", () -> "jdbc:mariadb://" + host + ":" + port + "/" + db);
        registry.add("itukt." + prefix + ".jdbc.username", () -> "root");
        registry.add("itukt." + prefix + ".jdbc.password", () -> password);
    }

    private static ComposeContainer runOutsideDocker(File composeFile) {
        return new ComposeContainer(composeFile)
                .withServices("app-db", "stamdata-db")
                .withExposedService("app-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withExposedService("stamdata-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLocalCompose(true);
    }

    private static ComposeContainer runInDocker(File composeFile) {
        return new ComposeContainer(composeFile)
                .withServices("app-db", "stamdata-db", "validation-component")
                .withExposedService("app-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("app-db", new Slf4jLogConsumer(logger).withPrefix("app-db"))
                .withExposedService("stamdata-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("stamdata-db", new Slf4jLogConsumer(logger).withPrefix("stamdata-db"))
                .withExposedService("validation-component", 8080, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("validation-component", new Slf4jLogConsumer(logger).withPrefix("validation-component"))
                .withLocalCompose(false);
    }

    private static void resetDB(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SHOW FULL TABLES WHERE Table_type = 'BASE TABLE'");

        for (Map<String, Object> row : rows) {
            String tableName = (String) row.values().toArray()[0];
            if (!tableName.endsWith("_seq")) {
                jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
            }
        }

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
    }

    @BeforeEach
    void setupApiClient() {
        String host = runInDocker ? environment.getServiceHost("validation-component", 8080) : "localhost";
        String port = runInDocker ? environment.getServicePort("validation-component", 8080).toString() : String.valueOf(localServerPort);
        client = new ApiClient().setBasePath(String.format("http://%s:%s", host, port));
    }

    @AfterEach
    void cleanup(@Autowired @Qualifier("appDataSource") DataSource dataSource) {
        resetDB(dataSource);
    }


}
