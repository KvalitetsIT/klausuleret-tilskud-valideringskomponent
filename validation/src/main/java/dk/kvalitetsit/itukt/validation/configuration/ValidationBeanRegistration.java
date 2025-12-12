package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.configuration.DataSourceBuilder;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepositoryImpl;
import dk.kvalitetsit.itukt.validation.stamdata.repository.mapping.DrugClauseViewMapper;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.DrugClauseCache;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.DrugClauseCacheImpl;
import dk.kvalitetsit.itukt.validation.service.*;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DrugClauseViewRepository;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DrugClauseViewRepositoryAdaptor;
import dk.kvalitetsit.itukt.validation.stamdata.repository.DrugClauseViewRepositoryImpl;
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
    public DrugClauseViewRepository drugClauseRepository(@Qualifier("stamDataSource") DataSource dataSource) {
        return new DrugClauseViewRepositoryImpl(dataSource);
    }

    @Bean
    public DrugClauseViewRepositoryAdaptor drugClauseViewRepositoryAdaptor(DrugClauseViewRepository drugClauseViewRepository) {
        DrugClauseViewMapper mapper = new DrugClauseViewMapper();
        return new DrugClauseViewRepositoryAdaptor(mapper, drugClauseViewRepository);
    }

    @Bean
    public DrugClauseCache drugClauseCache(DrugClauseViewRepositoryAdaptor stamDataRepository) {
        return new DrugClauseCacheImpl(configuration.stamdata().cache(), stamDataRepository);
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
            @Autowired DrugClauseCache stamDataCache,
            @Autowired SkippedValidationService skippedValidationService
    ) {
        return new ValidationServiceAdaptor(
                new ValidationServiceImpl(
                        clauseService,
                        stamDataCache,
                        skippedValidationService
                )
        );
    }

}