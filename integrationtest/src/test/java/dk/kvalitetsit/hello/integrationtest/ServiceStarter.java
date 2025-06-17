package dk.kvalitetsit.hello.integrationtest;

import dk.kvalitetsit.hello.Application;
import org.jetbrains.annotations.NotNull;
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

public class ServiceStarter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarter.class);
    private static final Logger mariadbLogger = LoggerFactory.getLogger("mariadb");

    private static boolean isRunning = false;

    public static URI startServices() {
        if(!isRunning) {
            var jdbcUrl = setupDatabaseContainer();
            System.setProperty("JDBC.URL", jdbcUrl);
            System.setProperty("JDBC.USER", "hellouser");
            System.setProperty("JDBC.PASS", "secret1234");

            SpringApplication.run(Application.class);
            isRunning = true;
        }
        try {
            return new URI("http://localhost:8080");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String setupDatabaseContainer() {
        // Database server.
        var mariadb = new MariaDBContainer<>("mariadb:10.6")
                .withDatabaseName("hellodb")
                .withUsername("hellouser")
                .withPassword("secret1234")
                .withNetworkAliases("mariadb");
        mariadb.start();
        attachLogger(mariadbLogger, mariadb);
        return mariadb.getJdbcUrl();
    }

    private static void attachLogger(Logger logger, GenericContainer<?> container) {
        ServiceStarter.logger.info("Attaching logger to container: {}", container.getContainerInfo().getName());
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }

    public record RunningCompose(URI helloService, ComposeContainer composeContainer) {}

    public static RunningCompose startDockerCompose() {
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
            URI helloService = new URI("http://" + containerHost + ":" + containerPort);
            return new RunningCompose(helloService, composeContainer);
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