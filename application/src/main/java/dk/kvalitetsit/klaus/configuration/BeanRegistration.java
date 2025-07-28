package dk.kvalitetsit.klaus.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.boundary.mapping.*;
import dk.kvalitetsit.klaus.repository.ClauseDao;
import dk.kvalitetsit.klaus.repository.ClauseDaoAdaptor;
import dk.kvalitetsit.klaus.repository.mapping.ClauseEntityMapper;
import dk.kvalitetsit.klaus.repository.mapping.EntityClauseMapper;
import dk.kvalitetsit.klaus.repository.mapping.EntityExpressionMapper;
import dk.kvalitetsit.klaus.repository.mapping.ExpressionEntityMapper;
import dk.kvalitetsit.klaus.service.model.Prescription;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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
    public ClauseDaoAdaptor clauseDaoAdaptor(ClauseDao dao) {
        return new ClauseDaoAdaptor(dao, new ClauseEntityMapper(new ExpressionEntityMapper()), new EntityClauseMapper(new EntityExpressionMapper()));
    }

    @Bean
    public PlatformTransactionManager masterTransactionManager(@Qualifier("masterDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public Mapper<org.openapitools.model.Clause, dk.kvalitetsit.klaus.model.Clause> dtoMapper() {
        return new DtoClauseMapper(new ExpressionMapper());
    }

    @Bean
    public Mapper<dk.kvalitetsit.klaus.model.Clause, org.openapitools.model.Clause> clauseDtoMapper() {
        return new ClauseDtoMapper(new ExpressionModelMapper());
    }

    @Bean
    public Mapper<String, org.openapitools.model.Clause> dslMapper() {
        return new DslMapper();
    }

    @Bean
    public Mapper<dk.kvalitetsit.klaus.model.Clause, String> modelDslMapper() {
        return new ClauseDslMapper(new ExpressionDslMapper());
    }

    @Bean
    public Mapper<org.openapitools.model.Prescription, Prescription> precriptionModelMapper() {
        return (org.openapitools.model.Prescription prescription) -> new Prescription(
                prescription.getCreatedBy().orElse(null),
                prescription.getReportedBy().orElse(null),
                prescription.getDrugIdentifier().orElse(null),
                prescription.getIndicationCode().orElse(null),
                prescription.getCreatedDateTime().map(OffsetDateTime::toInstant).orElse(null)
        );
    }

    @Bean
    public Mapper<Prescription, org.openapitools.model.Prescription> precriptionDtoMapper() {
        return (Prescription prescription) -> new org.openapitools.model.Prescription()
                .createdBy(prescription.createdBy())
                .reportedBy(prescription.reportedBy())
                .drugIdentifier(prescription.drugIdentifier())
                .indicationCode(prescription.indicationCode())
                .createdDateTime(prescription.createdDateTime().atOffset(ZoneOffset.UTC));
    }


}
