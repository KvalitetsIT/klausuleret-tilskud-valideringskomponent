package dk.kvalitetsit.itukt.integrationtest;

public sealed interface Component permits OutsideDockerComponent, InDockerComponent {
    void start();

    void stop();

    String getHost();

    Integer getPort();

}