package dk.kvalitetsit.itukt.management.configuration;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.DslClauseMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ExpressionDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.DtoClauseMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.DtoExpressionMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ExpressionModelDtoMapper;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.EntityClauseMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.EntityExpressionMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ClauseEntityMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ExpressionEntityMapper;
import dk.kvalitetsit.itukt.management.service.ManagementService;
import dk.kvalitetsit.itukt.management.service.ManagementServiceAdaptor;
import dk.kvalitetsit.itukt.management.service.ManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(ManagementConfiguration.class)
public class ManagementBeanRegistration {

    private final ManagementConfiguration configuration;

    public ManagementBeanRegistration(ManagementConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public ClauseRepository<ClauseEntity> clauseRepository(@Qualifier("appDataSource") DataSource dataSource){
        return new ClauseRepositoryImpl(dataSource);
    }

    @Bean
    public ClauseRepositoryAdaptor clauseRepositoryAdaptor(@Autowired ClauseRepository<ClauseEntity> clauseRepository) {
        return new ClauseRepositoryAdaptor(clauseRepository, new ClauseEntityMapper(new ExpressionEntityMapper()), new EntityClauseMapper(new EntityExpressionMapper()));
    }

    @Bean
    public ManagementService<Clause> managementService(@Autowired ClauseRepositoryAdaptor clauseRepository){
        return new ManagementServiceImpl(clauseRepository);
    }

    @Bean
    public Mapper<org.openapitools.model.Clause, Clause> dtoClauseMapper() {
        return new DtoClauseMapper(new DtoExpressionMapper());
    }

    @Bean
    public Mapper<String, org.openapitools.model.Clause> dslClauseMapper() {
        return new DslClauseMapper();
    }

    @Bean
    public Mapper<Clause, String> modelDslMapper() {
        return new ClauseDslMapper(new ExpressionDslMapper());
    }

    @Bean
    public Mapper<Clause, org.openapitools.model.Clause> clauseModelDtoMapper() {
        return new ClauseModelDtoMapper(new ExpressionModelDtoMapper());
    }

    @Bean
    public ManagementServiceAdaptor managementServiceAdaptor(
            @Autowired ManagementService<Clause> managementService,
            @Autowired Mapper<org.openapitools.model.Clause, Clause> dtoClauseMapper,
            @Autowired Mapper<String, org.openapitools.model.Clause> dslClauseMapper,
            @Autowired Mapper<Clause, String> modelDslMapper,
            @Autowired Mapper<Clause, org.openapitools.model.Clause> clauseModelDtoMapper
    ) {
        return new ManagementServiceAdaptor(
                managementService,
                dtoClauseMapper,
                clauseModelDtoMapper,
                dslClauseMapper,
                modelDslMapper
        );
    }

}