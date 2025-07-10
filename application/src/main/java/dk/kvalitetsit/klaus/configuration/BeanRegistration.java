package dk.kvalitetsit.klaus.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.boundary.mapping.DslMapper;
import dk.kvalitetsit.klaus.boundary.mapping.ExpressionToDslMapper;
import dk.kvalitetsit.klaus.boundary.mapping.DtoMapper;
import dk.kvalitetsit.klaus.boundary.mapping.ModelMapper;
import dk.kvalitetsit.klaus.service.model.Prescription;
import org.openapitools.model.Expression;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        hikariConfig.setJdbcUrl(configuration.jdbc().validation().url());
        hikariConfig.setUsername(configuration.jdbc().validation().username());
        hikariConfig.setPassword(configuration.jdbc().validation().password());
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public PlatformTransactionManager clauseTransactionManager(@Qualifier("validationDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("masterDataSource")
    public DataSource masterDataSource() {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(configuration.jdbc().master().url());
        hikariConfig.setUsername(configuration.jdbc().master().username());
        hikariConfig.setPassword(configuration.jdbc().master().password());
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public PlatformTransactionManager masterTransactionManager(@Qualifier("masterDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public Mapper<org.openapitools.model.Expression, dk.kvalitetsit.klaus.model.Expression> dtoMapper() {
        return new DtoMapper();
    }

    @Bean
    public Mapper<dk.kvalitetsit.klaus.model.Expression, org.openapitools.model.Expression> modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Mapper<String, Expression> dslMapper() {
        return new DslMapper();
    }

    @Bean
    public Mapper<dk.kvalitetsit.klaus.model.Expression, String> dslToModelMapper() {
        return new ExpressionToDslMapper();
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
