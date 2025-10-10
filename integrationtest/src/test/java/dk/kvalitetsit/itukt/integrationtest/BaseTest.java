package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.ExpressionRepositoryImpl;
import org.junit.jupiter.api.*;
import org.openapitools.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static final ComposeContainer dbEnvironment = new ComposeContainer(getComposeFile("docker-compose.db.yaml"))
            .withServices("itukt-db", "stamdata-db")
            .withExposedService("itukt-db", 3306, Wait.forHealthcheck())
            .withExposedService("stamdata-db", 3306, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(60)))
            .withLogConsumer("itukt-db", new Slf4jLogConsumer(logger).withPrefix("itukt-db"))
            .withLogConsumer("stamdata-db", new Slf4jLogConsumer(logger).withPrefix("stamdata-db"))
            .withLocalCompose(true);
    protected static Database appDatabase;
    protected static Database stamDatabase;

    protected Component component;
    protected ApiClient client;

    @BeforeAll
    void beforeAll() {
        dbEnvironment.start();
        appDatabase = getDatabase("itukt-db", "itukt_db", "rootroot");
        stamDatabase = getDatabase("stamdata-db", "sdm_krs_a", "");
    }

    @BeforeEach
    void setupApp() {
        boolean runInDocker = Boolean.getBoolean("runInDocker");
        component = runInDocker ? new InDockerComponent(logger) : new OutsideDockerComponent();


        this.load(new ClauseRepositoryImpl(appDatabase.getDatasource(), new ExpressionRepositoryImpl(appDatabase.getDatasource())));

        logger.info("Starting component");
        component.start();

        // Configure API client
        client = new ApiClient().setBasePath(String.format("http://%s:%s", component.getHost(), component.getPort()));
    }

    @AfterEach
    void afterEach() { // Clear both db and cache between each test
        appDatabase.clear();
        if (component != null) {
            component.stop();
        }
    }

    public static File getComposeFile(String fileName) {
        var testWorkingDir = System.getProperty("user.dir");
        var projectRoot = Paths.get(testWorkingDir).toAbsolutePath().normalize().getParent().toFile();
        return new File(projectRoot, "compose/development/" + fileName);
    }

    private static Database getDatabase(String serviceName, String dbName, String password) {
        var host = dbEnvironment.getServiceHost(serviceName, 3306);
        var port = dbEnvironment.getServicePort(serviceName, 3306);
        return new Database(host, port, dbName, "root", password);
    }

    /**
     * Loads data before component initialization
     * This is required since the caches is loaded during bean initialization on startup
     * NOTE: this is invoked before the component has been initialized
     *
     * @param repository with a datasource injected
     */
    protected abstract void load(ClauseRepository repository);


}
