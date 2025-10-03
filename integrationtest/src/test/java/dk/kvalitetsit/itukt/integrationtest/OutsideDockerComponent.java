package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.ComposeContainer;

import java.util.Properties;

import static dk.kvalitetsit.itukt.integrationtest.BaseTest.environment;

final class OutsideDockerComponent implements Component {
    private ConfigurableApplicationContext app;

    private static Properties getProperties() {
        Properties registry = new Properties();
        registerDatabaseProperties("stamdata-db", "validation.stamdata.stamdatadb", "sdm_krs_a", "", registry);
        registerDatabaseProperties("itukt-db", "common.ituktdb", "itukt_db", "rootroot", registry);
        registry.setProperty("itukt.validation.stamdata.cache.cron", "0 0 0 * * *");
        registry.setProperty("itukt.management.clause.cache.cron", "0 0 0 * * *");
        return registry;
    }

    private static void registerDatabaseProperties(
            String serviceName,
            String prefix,
            String db,
            String password,
            Properties registry
    ) {
        String host = environment.getServiceHost(serviceName, 3306);
        Integer port = environment.getServicePort(serviceName, 3306);
        registry.setProperty("itukt." + prefix + ".url", "jdbc:mariadb://" + host + ":" + port + "/" + db);
        registry.setProperty("itukt." + prefix + ".username", "root");
        registry.setProperty("itukt." + prefix + ".password", password);
    }

    @Override
    public void start() {
        System.getProperties().putAll(getProperties());
        app = SpringApplication.run(Application.class);
    }

    @Override
    public void stop() {
        if (app != null) {
            app.close();
        }
    }

    @Override
    public String getHost() {
        return "localhost";
    }

    @Override
    public Integer getPort() {
        return 8080;
    }
}

