package dk.kvalitetsit.klaus.integrationtest;

import dk.kvalitetsit.klaus.Application;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.ValidationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Duration;

public class ValidationIT {

    static ComposeContainer environment;
    private static ApiClient client;
    private static final Logger logger = LoggerFactory.getLogger(ValidationIT.class);

    @BeforeAll
    static void setup() {
        var testWorkingDir = System.getProperty("user.dir");
        var projectRoot = Paths.get(testWorkingDir).toAbsolutePath().normalize().getParent().toFile();
        var composeFile = new File(projectRoot, "documentation/docker/compose/docker-compose.yml");

        if (Boolean.getBoolean("runInDocker")) {
            runInDocker(composeFile);

        } else {
            runOutsideDocker(composeFile);
        }
    }

    private static void runOutsideDocker(File composeFile) {
        environment = new ComposeContainer(composeFile)
                .withServices("validation-db", "master-db")
                .withExposedService("validation-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withExposedService("master-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLocalCompose(true);

        environment.start();

        setupProperties("master-db", "master", "sdm_krs_a", "");
        setupProperties("validation-db", "validation", "hellodb", "rootroot");

        SpringApplication.run(Application.class);
        try {
            var uri = new URI("http://localhost:8080");
            client = new ApiClient().setBasePath(uri.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runInDocker(File composeFile) {

        environment = new ComposeContainer(composeFile)
                .withServices("validation-db", "master-db", "validation-component")
                .withExposedService("validation-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("validation-db", new Slf4jLogConsumer(logger).withPrefix("validation-db"))
                .withExposedService("master-db", 3306, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("master-db", new Slf4jLogConsumer(logger).withPrefix("master-db"))
                .withExposedService("validation-component", 8080, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("validation-component", new Slf4jLogConsumer(logger).withPrefix("validation-component"))
                .withLocalCompose(true);

        environment.start();

        String containerHost = environment.getServiceHost("validation-component", 8080);
        int containerPort = environment.getServicePort("validation-component", 8080);
        try {
            var uri = new URI("http://" + containerHost + ":" + containerPort);
            client = new ApiClient().setBasePath(uri.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setupProperties(String serviceName, String prefix, String db, String password) {
        String validationHost = environment.getServiceHost(serviceName, 3306);
        Integer validationPort = environment.getServicePort(serviceName, 3306);
        System.setProperty(String.format("app.jdbc.%s.url", prefix), String.format("jdbc:mariadb://%s:%s/%s", validationHost, validationPort, db));
        System.setProperty(String.format("app.jdbc.%s.username", prefix), "root");
        System.setProperty(String.format("app.jdbc.%s.password", prefix), password);
    }

    @AfterAll
    public static void afterAll() {
        environment.stop();
        environment.close();
    }

    @Test
    void testPostValidation()  {
        var api  =  new ValidationApi(new ApiClient());
        var request = new ValidationRequest().age(18);
        try {
            var response = api.v1ValidationsPost(request);
            Assertions.assertTrue(response);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}
