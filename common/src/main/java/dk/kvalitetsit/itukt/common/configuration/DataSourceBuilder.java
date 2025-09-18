package dk.kvalitetsit.itukt.common.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.Optional;

public class DataSourceBuilder {
    public DataSource build(DatasourceConfiguration config) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.url());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        Optional.ofNullable(config.connection().maxAge()).map(Duration::toMillis).ifPresent(hikariConfig::setMaxLifetime);;
        Optional.ofNullable(config.connection().testQuery()).ifPresent(hikariConfig::setConnectionTestQuery);
        Optional.ofNullable(config.connection().maxIdleTime()).map(Duration::toMillis).ifPresent(hikariConfig::setIdleTimeout);

        return new HikariDataSource(hikariConfig);
    }
}
