package dk.kvalitetsit.hello.dao;

import dk.kvalitetsit.hello.configuration.DatabaseConfiguration;
import dk.kvalitetsit.hello.configuration.TestConfiguration;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;

@SpringBootTest(classes = {TestConfiguration.class, DatabaseConfiguration.class})
@Transactional
abstract class AbstractDaoTest {
    private static Object initialized = null;

    @BeforeAll
    public static void setupMariadbJdbcUrl() {
        if (initialized == null) {
            var username = "hellouser";
            var password = "secret1234";

            MariaDBContainer mariadb = new MariaDBContainer("mariadb:10.6")
                    .withDatabaseName("hellodb")
                    .withUsername(username)
                    .withPassword(password);
            mariadb.start();
            String jdbcUrl = mariadb.getJdbcUrl();

            Flyway flyway = Flyway.configure()
                    .dataSource(jdbcUrl, username, password)
                    .load();
            flyway.migrate();

            System.setProperty("jdbc.url", jdbcUrl);
            System.setProperty("jdbc.user", username);
            System.setProperty("jdbc.pass", password);

            initialized = new Object();
        }
    }
}

