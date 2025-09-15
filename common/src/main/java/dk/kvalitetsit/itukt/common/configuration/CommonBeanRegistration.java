package dk.kvalitetsit.itukt.common.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.io.IOException;

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
    public OncePerRequestFilter oncePerRequestFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                long startTime = System.currentTimeMillis();

                try {
                    filterChain.doFilter(request, response);
                } finally {
                    long duration = System.currentTimeMillis() - startTime;
                    System.out.printf(
                            "Request { id: %s, Method: %s, Uri: %s, Response: %d, Time: %d ms }\n",
                            request.getRequestId(),
                            request.getMethod(),
                            request.getRequestURI(),
                            response.getStatus(),
                            duration
                    );
                }
            }
        };
    }
}