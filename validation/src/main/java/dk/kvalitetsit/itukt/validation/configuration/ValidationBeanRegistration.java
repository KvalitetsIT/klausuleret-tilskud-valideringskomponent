package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.configuration.DataSourceBuilder;
import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.mapping.DepartmentMapper;
import dk.kvalitetsit.itukt.validation.mapping.StamDataMapper;
import dk.kvalitetsit.itukt.validation.repository.*;
import dk.kvalitetsit.itukt.validation.repository.cache.Cache;
import dk.kvalitetsit.itukt.validation.repository.cache.DepartmentCacheImpl;
import dk.kvalitetsit.itukt.validation.repository.cache.StamdataCacheImpl;
import dk.kvalitetsit.itukt.validation.repository.entity.DepartmentEntity;
import dk.kvalitetsit.itukt.validation.service.*;
import dk.kvalitetsit.itukt.validation.service.model.StamData;
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
    public Repository<StamDataEntity> stamDataRepository(@Qualifier("stamDataSource") DataSource dataSource) {
        return new StamDataRepository(dataSource);
    }

    @Bean
    public StamDataRepositoryAdaptor stamDataRepositoryAdaptor(Repository<StamDataEntity> stamDataRepository) {
        StamDataMapper mapper = new StamDataMapper();
        return new StamDataRepositoryAdaptor(mapper, stamDataRepository);
    }

    @Bean
    public Cache<Long, StamData> stamDataCache(StamDataRepositoryAdaptor stamDataRepository) {
        return new StamdataCacheImpl(configuration.stamdata().cache(), stamDataRepository);
    }

    @Bean
    public Repository<DepartmentEntity> departmentRepository(@Qualifier("stamDataSource") DataSource dataSource) {
        return new DepartmentRepository(dataSource);
    }

    @Bean
    public DepartmentRepositoryAdaptor departmentRepositoryAdaptor(Repository<DepartmentEntity> departmentRepository) {
        DepartmentMapper mapper = new DepartmentMapper();
        return new DepartmentRepositoryAdaptor(mapper, departmentRepository);
    }

    @Bean
    public Cache<Department.Identifier, Department> departmentCache(DepartmentRepositoryAdaptor adaptor) {
        return new DepartmentCacheImpl(configuration.stamdata().cache(), adaptor);
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
            @Autowired Cache<Long, StamData> stamDataCache,
            @Autowired SkippedValidationService skippedValidationService,
            @Autowired Cache<Department.Identifier, Department> departmentCache
    ) {
        return new ValidationServiceAdaptor(
                departmentCache,
                new ValidationServiceImpl(
                        clauseService,
                        stamDataCache,
                        skippedValidationService
                )
        );
    }

}