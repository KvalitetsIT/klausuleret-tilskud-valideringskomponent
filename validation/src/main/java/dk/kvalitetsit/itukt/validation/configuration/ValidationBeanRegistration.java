package dk.kvalitetsit.itukt.validation.configuration;

import dk.kvalitetsit.itukt.common.configuration.DataSourceBuilder;
import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.mapping.ActorDtoModelMapper;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepositoryImpl;
import dk.kvalitetsit.itukt.validation.service.*;
import dk.kvalitetsit.itukt.validation.stamdata.repository.*;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.Cache;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.DepartmentCacheImpl;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.DrugClauseCacheImpl;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DepartmentEntity;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import dk.kvalitetsit.itukt.validation.stamdata.repository.mapping.DepartmentEntityModelMapper;
import dk.kvalitetsit.itukt.validation.stamdata.repository.mapping.DrugClauseViewMapper;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;
import org.openapitools.model.ValidationRequest;
import org.openapitools.model.ValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Set;

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
    public Repository<DrugClauseView> drugClauseRepository(@Qualifier("stamDataSource") DataSource dataSource) {
        return new DrugClauseViewRepositoryImpl(dataSource);
    }

    @Bean
    public Repository<DrugClause> drugClauseViewRepositoryAdaptor(Repository<DrugClauseView> drugClauseViewRepository) {
        DrugClauseViewMapper mapper = new DrugClauseViewMapper();
        return new DrugClauseViewRepositoryAdaptor(mapper, drugClauseViewRepository);
    }

    @Bean
    public Cache<Long, DrugClause> drugClauseCache(Repository<DrugClause> stamDataRepository) {
        return new DrugClauseCacheImpl(configuration.stamdata().cache(), stamDataRepository);
    }

    @Bean
    public Repository<DepartmentEntity> departmentRepository(@Qualifier("stamDataSource") DataSource dataSource) {
        return new DepartmentRepository(dataSource);
    }

    @Bean
    public DepartmentRepositoryAdaptor departmentRepositoryAdaptor(Repository<DepartmentEntity> departmentRepository) {
        DepartmentEntityModelMapper mapper = new DepartmentEntityModelMapper();
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
            @Autowired Cache<Long, DrugClause> drugClauseCache,
            @Autowired SkippedValidationService skippedValidationService,
            @Autowired Cache<Department.Identifier, Department> departmentCache
    ) {
        return new ValidationServiceAdaptor(
                new ValidationServiceImpl(
                        clauseService,
                        drugClauseCache,
                        skippedValidationService,
                        departmentCache
                ),
                new ActorDtoModelMapper()
        );
    }

}