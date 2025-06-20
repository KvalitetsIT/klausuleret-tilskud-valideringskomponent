package dk.kvalitetsit.hello.integrationtest;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.KithugsApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class IntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    static {
        setLanguage();
    }

    private static void setLanguage() {
        //On certain computers the language for Spring error messages defaults to Danish.
        //This would cause certain tests where we compare error messages to strings to fail.
        //This forces the language of error messages to English, preventing any issues.
        Locale.setDefault(Locale.ENGLISH);
    }

    public static KithugsApi startService() {
        var runInDocker = Boolean.getBoolean("runInDocker");
        logger.info("Running integration test in docker container: {}", runInDocker);
        var uri = runInDocker ? ServiceStarter.startDockerCompose().helloService() : ServiceStarter.startServices();
        var apiClient = new ApiClient(); apiClient.setBasePath(uri.toString());
        return new KithugsApi(apiClient);
    }
}