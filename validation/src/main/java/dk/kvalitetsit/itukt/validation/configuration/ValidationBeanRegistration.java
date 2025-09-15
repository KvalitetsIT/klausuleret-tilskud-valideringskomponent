package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.configuration.DataSourceBuilder;
import dk.kvalitetsit.itukt.common.model.StamData;
import dk.kvalitetsit.itukt.common.repository.cache.ClauseCache;
import dk.kvalitetsit.itukt.common.repository.cache.StamdataCache;
import dk.kvalitetsit.itukt.validation.StamDataMapper;
import dk.kvalitetsit.itukt.validation.repository.*;
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
    public StamDataRepository<StamDataEntity> stamDataRepository(@Qualifier("stamDataSource") DataSource dataSource) {
        return new StamDataRepositoryImpl(dataSource);
    }

    @Bean
    public StamDataRepository<StamData> stamDataRepositoryAdaptor(StamDataRepository<StamDataEntity> stamDataRepository) {
        Mapper<StamDataEntity, StamData> mapper = new StamDataMapper();
        return new StamDataRepositoryAdaptor(mapper, stamDataRepository);
    }

    @Bean
    public StamdataCache stamDataCache(StamDataRepository<StamData> stamDataRepository) {
        return new StamdataCacheImpl(configuration.stamdata().cache(), stamDataRepository);
    }

    @Bean
    public ValidationService<ValidationRequest, ValidationResponse> validationService(
            @Autowired ClauseCache clauseCache,
            @Autowired StamdataCache stamDataCache
    ) {
        return new ValidationServiceAdaptor(
                new ValidationServiceImpl(clauseCache, stamDataCache)
        );
    }

}