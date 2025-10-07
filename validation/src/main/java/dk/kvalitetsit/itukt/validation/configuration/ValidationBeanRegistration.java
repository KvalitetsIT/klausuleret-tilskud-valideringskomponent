package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.configuration.DataSourceBuilder;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.mapping.StamDataMapper;
import dk.kvalitetsit.itukt.validation.repository.*;
import dk.kvalitetsit.itukt.validation.repository.cache.StamdataCache;
import dk.kvalitetsit.itukt.validation.repository.cache.StamdataCacheImpl;
import dk.kvalitetsit.itukt.validation.service.*;
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
    public StamDataRepositoryAdaptor stamDataRepositoryAdaptor(StamDataRepository stamDataRepository) {
        StamDataMapper mapper = new StamDataMapper();
        return new StamDataRepositoryAdaptor(mapper, stamDataRepository);
    }

    @Bean
    public StamdataCache stamDataCache(StamDataRepositoryAdaptor stamDataRepository) {
        return new StamdataCacheImpl(configuration.stamdata().cache(), stamDataRepository);
    }

    @Bean
    public SkippedValidationRepository skippedValidationRepository(@Qualifier("appDataSource") DataSource dataSource) {
        return new SkippedValidationRepositoryImpl(dataSource);
    }

    @Bean
    public SkippedValidationService skippedValidationService(
            @Autowired SkippedValidationRepository skippedValidationRepository,
            @Autowired ClauseService clauseService
    ) {
        return new SkippedValidationServiceImpl(skippedValidationRepository, clauseService);
    }

    @Bean
    public ValidationService<ValidationRequest, ValidationResponse> validationService(
            @Autowired ClauseService clauseService,
            @Autowired StamdataCache stamDataCache,
            @Autowired SkippedValidationService skippedValidationService
    ) {
        return new ValidationServiceAdaptor(
                new ValidationServiceImpl(
                        clauseService,
                        stamDataCache,
                        skippedValidationService)
        );
    }

}