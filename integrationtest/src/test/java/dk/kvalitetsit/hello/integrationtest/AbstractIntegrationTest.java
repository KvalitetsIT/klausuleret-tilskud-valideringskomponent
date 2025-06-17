package dk.kvalitetsit.hello.integrationtest;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Locale;

abstract class AbstractIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);
    private static URI apiBasePath;

    @BeforeAll
    static void beforeClass() {
        setup();
    }

    private static void setup() {
        setLanguage();
        var runInDocker = Boolean.getBoolean("runInDocker");
        logger.info("Running integration test in docker container: {}", runInDocker);
        apiBasePath = runInDocker ? ServiceStarter.startDockerCompose().helloService() : ServiceStarter.startServices();
    }

    private static void setLanguage() {
        //On certain computers the language for Spring error messages defaults to Danish.
        //This would cause certain tests where we compare error messages to strings to fail.
        //This forces the language of error messages to English, preventing any issues.
        Locale.setDefault(Locale.ENGLISH);
    }

    static URI getApiBasePath() {
        return apiBasePath;
    }
}