package dk.kvalitetsit.itukt.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dk.kvalitetsit.itukt.Mapper;
import dk.kvalitetsit.itukt.boundary.mapping.dsl.ClauseDslMapper;
import dk.kvalitetsit.itukt.boundary.mapping.dsl.DslClauseMapper;
import dk.kvalitetsit.itukt.boundary.mapping.dsl.ExpressionDslMapper;
import dk.kvalitetsit.itukt.boundary.mapping.dto.DtoClauseMapper;
import dk.kvalitetsit.itukt.boundary.mapping.dto.DtoExpressionMapper;
import dk.kvalitetsit.itukt.boundary.mapping.model.ClauseModelDtoMapper;
import dk.kvalitetsit.itukt.boundary.mapping.model.ExpressionModelDtoMapper;
import dk.kvalitetsit.itukt.repository.ClauseRepository;
import dk.kvalitetsit.itukt.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.repository.mapping.entity.EntityClauseMapper;
import dk.kvalitetsit.itukt.repository.mapping.entity.EntityExpressionMapper;
import dk.kvalitetsit.itukt.repository.mapping.model.ClauseEntityMapper;
import dk.kvalitetsit.itukt.repository.mapping.model.ExpressionEntityMapper;
import dk.kvalitetsit.itukt.service.model.DataContext;
import org.openapitools.model.ExistingDrugMedication;
import org.openapitools.model.ValidationRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Configuration
public class BeanRegistration {

    private final MainConfiguration configuration;

    public BeanRegistration(MainConfiguration configuration) {
        this.configuration = configuration;
    }

    @Primary
    @Bean("validationDataSource")
    public DataSource validationDataSource() {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(configuration.management().jdbc().url());
        hikariConfig.setUsername(configuration.management().jdbc().username());
        hikariConfig.setPassword(configuration.management().jdbc().password());
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public PlatformTransactionManager clauseTransactionManager(@Qualifier("validationDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("masterDataSource")
    public DataSource masterDataSource() {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(configuration.validation().jdbc().url());
        hikariConfig.setUsername(configuration.validation().jdbc().username());
        hikariConfig.setPassword(configuration.validation().jdbc().password());
        return new HikariDataSource(hikariConfig);
    }

    @Bean("masterJdbcTemplate")
    public NamedParameterJdbcTemplate masterJdbcTemplate(@Qualifier("masterDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public ClauseRepositoryAdaptor clauseDaoAdaptor(ClauseRepository dao) {
        return new ClauseRepositoryAdaptor(dao, new ClauseEntityMapper(new ExpressionEntityMapper()), new EntityClauseMapper(new EntityExpressionMapper()));
    }

    @Bean
    public PlatformTransactionManager masterTransactionManager(@Qualifier("masterDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public Mapper<org.openapitools.model.Clause, dk.kvalitetsit.itukt.model.Clause> dtoMapper() {
        return new DtoClauseMapper(new DtoExpressionMapper());
    }

    @Bean
    public Mapper<dk.kvalitetsit.itukt.model.Clause, org.openapitools.model.Clause> clauseDtoMapper() {
        return new ClauseModelDtoMapper(new ExpressionModelDtoMapper());
    }

    @Bean
    public Mapper<String, org.openapitools.model.Clause> dslMapper() {
        return new DslClauseMapper();
    }

    @Bean
    public Mapper<dk.kvalitetsit.itukt.model.Clause, String> modelDslMapper() {
        return new ClauseDslMapper(new ExpressionDslMapper());
    }

    @Bean
    public Mapper<ValidationRequest, DataContext> validationRequestDataContextMapper() {
        return entry -> new DataContext(Map.of(
                "ALDER", List.of(entry.getAge().toString()),
                "ATC", entry.getExistingDrugMedications().orElse(List.of()).stream().map(ExistingDrugMedication::getAtcCode).toList()
                ));
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
}