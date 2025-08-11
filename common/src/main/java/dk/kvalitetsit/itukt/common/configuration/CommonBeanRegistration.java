package dk.kvalitetsit.itukt.common.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(CommonConfiguration.class)
public class CommonBeanRegistration {

    private final CommonConfiguration configuration;

    public CommonBeanRegistration(CommonConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean("appDataSource")
    public DataSource appDataSource() {
        return createDataSource(
                configuration.jdbc().url(),
                configuration.jdbc().username(),
                configuration.jdbc().password()
        );
    }

    @Bean
    public PlatformTransactionManager clauseTransactionManager(@Qualifier("appDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public CorsFilter corsFilter(@Value("#{'${allowed_origins:http://localhost}'}") List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        allowedOrigins.forEach(config::addAllowedOrigin);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    private DataSource createDataSource(String url, String username, String password) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        return new HikariDataSource(hikariConfig);
    }

}