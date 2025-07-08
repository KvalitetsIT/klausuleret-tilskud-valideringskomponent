package dk.kvalitetsit.hello.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.Optional;

@Configuration
@EnableTransactionManagement
public class DatabaseConfiguration {

    @Bean
    public DataSource dataSource(@Value("${jdbc.url}") String jdbcUrl,
                                 @Value("${jdbc.user}") String jdbcUser,
                                 @Value("${jdbc.pass}") String jdbcPass,
                                 @Value("${jdbc.connection.test.query:#{null}}") String testQuery,
                                 @Value("${jdbc.connection.max.age:PT30M}") Duration maxConnectionAge,
                                 @Value("${jdbc.connection.max.idle.time:#{null}}") Duration maxConnectionIdleTime) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(jdbcUser);
        hikariConfig.setPassword(jdbcPass);
        Optional.ofNullable(testQuery).ifPresent(hikariConfig::setConnectionTestQuery);
        hikariConfig.setMaxLifetime(maxConnectionAge.toMillis());
        Optional.ofNullable(maxConnectionIdleTime).map(Duration::toMillis).ifPresent(hikariConfig::setIdleTimeout);

        return new HikariDataSource(hikariConfig);
    }
}
