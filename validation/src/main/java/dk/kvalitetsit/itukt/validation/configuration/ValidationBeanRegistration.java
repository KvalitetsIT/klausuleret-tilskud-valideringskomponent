package dk.kvalitetsit.itukt.validation.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import org.openapitools.model.ExistingDrugMedication;
import org.openapitools.model.ValidationRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(ValidationConfiguration.class)
public class ValidationBeanRegistration {

    private final ValidationConfiguration configuration;

    public ValidationBeanRegistration(ValidationConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean("stamDataSource")
    public DataSource stamDataSource() {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(configuration.stamdata().jdbc().url());
        hikariConfig.setUsername(configuration.stamdata().jdbc().username());
        hikariConfig.setPassword(configuration.stamdata().jdbc().password());
        return new HikariDataSource(hikariConfig);
    }

    @Bean("stamDataJdbcTemplate")
    public NamedParameterJdbcTemplate stamDataJdbcTemplate(@Qualifier("stamDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager stamDataTransactionManager(@Qualifier("stamDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public Mapper<ValidationRequest, DataContext> validationRequestDataContextMapper() {
        return entry -> new DataContext(Map.of(
                "ALDER", List.of(entry.getAge().toString()),
                "ATC", entry.getExistingDrugMedications().orElse(List.of()).stream().map(ExistingDrugMedication::getAtcCode).toList()
        ));
    }

}