package dk.kvalitetsit.itukt.integrationtest;

import org.slf4j.Logger;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

import static dk.kvalitetsit.itukt.integrationtest.BaseTest.getComposeFile;

final class InDockerComponent implements Component {
    private final ComposeContainer component;

    public InDockerComponent(Logger logger) {
        this.component = new ComposeContainer(getComposeFile())
                .withServices("validation-component")
                .withExposedService("validation-component", 8080, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer("validation-component", new Slf4jLogConsumer(logger).withPrefix("validation-component"))
                .withLocalCompose(false);
    }

    @Override
    public void start() {
        component.start();
    }

    @Override
    public void stop() {
        component.stop();
    }

    @Override
    public String getHost() {
        return component.getServiceHost("validation-component", 8080);
    }

    @Override
    public Integer getPort() {
        return component.getServicePort("validation-component", 8080);
    }
}