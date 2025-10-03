package dk.kvalitetsit.itukt.common.configuration;

import dk.kvalitetsit.itukt.common.filters.GenericExceptionHandler;
import dk.kvalitetsit.itukt.common.filters.RequestLogger;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.common.repository.cache.CacheScheduler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableTransactionManagement
public class CommonBeanRegistration {

    private final CommonConfiguration configuration;

    public CommonBeanRegistration(CommonConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public DataSourceBuilder dataSourceBuilder() {
        return new DataSourceBuilder();
    }

    @Bean("appDataSource")
    public DataSource appDataSource(DataSourceBuilder dataSourceBuilder) {
        return dataSourceBuilder.build(configuration.ituktdb());
    }

    @Bean
    public PlatformTransactionManager appTransactionManager(@Qualifier("appDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public CorsFilter corsFilter() {
        var corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        configuration.allowedOrigins().forEach(corsConfig::addAllowedOrigin);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source);
    }

    @Bean
    public OncePerRequestFilter requestLogger() {
        return new RequestLogger();
    }

    @Bean
    public OncePerRequestFilter genericExceptionHandler() {
        return new GenericExceptionHandler();
    }

    @Bean
    public ApplicationRunner cacheScheduler(List<CacheLoader> loaders) {
        return args -> CacheScheduler.init(loaders);
    }
}