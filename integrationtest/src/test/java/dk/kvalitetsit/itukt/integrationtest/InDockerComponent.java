package dk.kvalitetsit.itukt.integrationtest;

import org.slf4j.Logger;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.File;
import java.time.Duration;

final class InDockerComponent implements Component {
    private static final String SERVICE_NAME = "validation-component";
    private final WaitStrategy waitStrategy;
    private final File composeFile;
    private final Slf4jLogConsumer logConsumer;
    private ComposeContainer component;

    public InDockerComponent(Logger logger) {
        logConsumer = new Slf4jLogConsumer(logger).withPrefix(SERVICE_NAME);
        composeFile = BaseTest.getComposeFile("docker-compose.app.yaml");
        waitStrategy = Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60));
    }

    @Override
    public void start() {
        component = createComposeContainer();
        component.start();
    }

    private ComposeContainer createComposeContainer() {
        return new ComposeContainer(composeFile)
                .withServices(SERVICE_NAME)
                .withExposedService(SERVICE_NAME, 8080, waitStrategy)
                .withLogConsumer(SERVICE_NAME, logConsumer);
    }

    @Override
    public void stop() {
        component.stop();
    }

    @Override
    public String getHost() {
        return component.getServiceHost(SERVICE_NAME, 8080);
    }

    @Override
    public Integer getPort() {
        return component.getServicePort(SERVICE_NAME, 8080);
    }
}