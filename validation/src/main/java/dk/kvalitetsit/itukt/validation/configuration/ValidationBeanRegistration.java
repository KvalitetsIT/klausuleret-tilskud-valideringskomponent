package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.configuration.DataSourceBuilder;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepository;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepositoryImpl;
import dk.kvalitetsit.itukt.validation.service.ValidationService;
import dk.kvalitetsit.itukt.validation.service.ValidationServiceAdaptor;
import dk.kvalitetsit.itukt.validation.service.ValidationServiceImpl;
import org.openapitools.model.ValidationRequest;
import org.openapitools.model.ValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ValidationBeanRegistration {

    private final ValidationConfiguration configuration;

    public ValidationBeanRegistration(ValidationConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean("stamDataSource")
    public DataSource stamDataSource(DataSourceBuilder dataSourceBuilder) {
        return dataSourceBuilder.build(configuration.stamdata().stamdatadb());
    }



    @Bean
    public StamDataRepository stamDataRepository(@Qualifier("stamDataSource") DataSource dataSource) {
        return new StamDataRepositoryImpl(dataSource);
    }

    @Bean
    public StamDataCache stamDataCache(StamDataRepository stamDataRepository) {
        return new StamDataCache(configuration.stamdata().cache(), stamDataRepository);
    }

    @Bean
    public ValidationService<ValidationRequest, ValidationResponse> validationService(@Autowired ClauseCache clauseCache,
                                                                                      @Autowired StamDataCache stamDataCache) {
        return new ValidationServiceAdaptor(
                new ValidationServiceImpl(clauseCache, stamDataCache)
        );
    }

}