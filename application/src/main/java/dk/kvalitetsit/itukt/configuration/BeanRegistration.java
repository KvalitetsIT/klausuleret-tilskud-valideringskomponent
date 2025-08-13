package dk.kvalitetsit.itukt.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.DslClauseMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ExpressionDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.DtoClauseMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.DtoExpressionMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ExpressionModelDtoMapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.EntityClauseMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.EntityExpressionMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ClauseEntityMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ExpressionEntityMapper;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.service.ManagementService;
import dk.kvalitetsit.itukt.management.service.ManagementServiceAdaptor;
import dk.kvalitetsit.itukt.management.service.ManagementServiceImpl;
import dk.kvalitetsit.itukt.validation.boundary.mapping.ValidationDataContextMapper;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepository;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepositoryImpl;
import dk.kvalitetsit.itukt.validation.service.ValidationService;
import dk.kvalitetsit.itukt.validation.service.ValidationServiceAdaptor;
import dk.kvalitetsit.itukt.validation.service.ValidationServiceImpl;
import org.openapitools.model.ValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class BeanRegistration {

    private final MainConfiguration configuration;

    public BeanRegistration(MainConfiguration configuration) {
        this.configuration = configuration;
    }

    @Primary
    @Bean("appDataSource")
    public DataSource appDataSource() {
        return createDataSource(
                configuration.management().jdbc().url(),
                configuration.management().jdbc().username(),
                configuration.management().jdbc().password()
        );
    }

    @Bean("stamDataSource")
    public DataSource stamDataSource() {
        return createDataSource(
                configuration.validation().jdbc().url(),
                configuration.validation().jdbc().username(),
                configuration.validation().jdbc().password()
        );
    }



    @Bean
    public ClauseRepositoryAdaptor clauseRepositoryAdaptor(@Autowired ClauseRepository<ClauseEntity> clauseRepository) {
        return new ClauseRepositoryAdaptor(clauseRepository, new ClauseEntityMapper(new ExpressionEntityMapper()), new EntityClauseMapper(new EntityExpressionMapper()));
    }

    @Bean
    public PlatformTransactionManager clauseTransactionManager(@Qualifier("appDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public PlatformTransactionManager stamDataTransactionManager(@Qualifier("stamDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public ManagementService<Clause> managementService(@Autowired ClauseRepositoryAdaptor clauseRepository){
        return new ManagementServiceImpl(clauseRepository);
    }

    @Bean
    public ClauseRepository<ClauseEntity> clauseRepository(@Qualifier("appDataSource") DataSource dataSource){
        return new ClauseRepositoryImpl(dataSource);
    }

    @Bean
    public StamDataRepository stamDataRepository(@Qualifier("stamDataSource")  DataSource dataSource){
        return new StamDataRepositoryImpl(dataSource);
    }

    @Bean
    public ManagementServiceAdaptor managementServiceAdaptor(@Autowired ManagementService<Clause> managementService) {
        return new ManagementServiceAdaptor(
                managementService,
                new DtoClauseMapper(new DtoExpressionMapper()),
                new ClauseModelDtoMapper(new ExpressionModelDtoMapper()),
                new DslClauseMapper(),
                new ClauseDslMapper(new ExpressionDslMapper())
        );
    }

    @Bean
    public ValidationService<ValidationRequest> validationService(@Autowired ClauseRepositoryAdaptor clauseRepository) {
        return new ValidationServiceAdaptor(
                new ValidationServiceImpl(clauseRepository),
                new ValidationDataContextMapper()
        );
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