package dk.kvalitetsit.itukt.management.configuration;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ExpressionModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.ExpressionDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseInputDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ErrorModelDtoMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ExpressionModelDtoMapper;
import dk.kvalitetsit.itukt.management.repository.*;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.cache.ClauseCache;
import dk.kvalitetsit.itukt.management.repository.cache.ClauseCacheImpl;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.NotPersistedClauseModelEntityMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.NotPersistedExpressionEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.PersistedClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.PersistedExpressionEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.NotPersistedExpressionModelEntityMapper;
import dk.kvalitetsit.itukt.management.service.ClauseServiceImpl;
import dk.kvalitetsit.itukt.management.service.ManagementService;
import dk.kvalitetsit.itukt.management.service.ManagementServiceAdaptor;
import dk.kvalitetsit.itukt.management.service.ManagementServiceImpl;
import org.openapitools.model.ClauseOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ManagementBeanRegistration {

    private final ManagementConfiguration configuration;

    public ManagementBeanRegistration(ManagementConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public ClauseCache clauseCache(ClauseRepository clauseRepository) {
        return new ClauseCacheImpl(configuration.clause().cache(), clauseRepository);
    }

    @Bean
    public ExpressionRepository expressionRepository(@Qualifier("appDataSource") DataSource dataSource) {
        return new ExpressionRepositoryImpl(dataSource);
    }

    @Bean
    public ClauseRepository clauseRepository(@Qualifier("appDataSource") DataSource dataSource, ExpressionRepository expressionRepository) {
        return new ClauseRepositoryImpl(dataSource, expressionRepository);
    }

    @Bean
    public Mapper<ExpressionEntity.Persisted, Expression.Persisted> persistedExpressionEntityModelMapper() {
        return new PersistedExpressionEntityModelMapper();
    }

    @Bean
    public Mapper<ExpressionEntity.NotPersisted, Expression.NotPersisted> notPersistedExpressionEntityModelMapper() {
        return new NotPersistedExpressionEntityModelMapper();
    }


    @Bean
    public Mapper<ClauseEntity.Persisted, Clause.Persisted> persistedClauseEntityModelMapper(Mapper<ExpressionEntity.Persisted, Expression.Persisted> expressionMapper) {
        return new PersistedClauseEntityModelMapper(expressionMapper);
    }

    @Bean
    public Mapper<Expression.NotPersisted, ExpressionEntity.NotPersisted> notPersistedExpressionModelEntityMapper(){
        return new NotPersistedExpressionModelEntityMapper();
    }

    @Bean
    public Mapper<Clause.NotPersisted, ClauseEntity.NotPersisted> clauseEntityModelMapper(Mapper<Expression.NotPersisted, ExpressionEntity.NotPersisted> expressionMapper) {
        return new NotPersistedClauseModelEntityMapper(expressionMapper);
    }


    @Bean
    public ClauseService clauseService(ClauseCache cache, Mapper<ClauseEntity.Persisted, Clause.Persisted> mapper) {
        return new ClauseServiceImpl(cache, mapper);
    }

    @Bean
    public ClauseRepositoryAdaptor clauseRepositoryAdaptor(
            @Autowired ClauseRepository clauseRepository,
            Mapper<ClauseEntity.Persisted, Clause.Persisted> toModelMapper,
            Mapper<Clause.NotPersisted, ClauseEntity.NotPersisted> toEntityMapper
    ) {
        return new ClauseRepositoryAdaptor(clauseRepository, toModelMapper, toEntityMapper);
    }

    @Bean
    public ManagementService managementService(@Autowired ClauseRepositoryAdaptor clauseRepository) {
        return new ManagementServiceImpl(clauseRepository);
    }

    @Bean
    public ManagementServiceAdaptor managementServiceAdaptor(@Autowired ManagementService managementService) {
        var errorMapper = new ErrorModelDtoMapper();

        ExpressionModelDtoMapper expressionModelMapper = new ExpressionModelDtoMapper();

        Mapper<Clause.Persisted, ClauseOutput> clauseModelDtoMapper = new ClauseModelDtoMapper(
                expressionModelMapper,
                errorMapper
        );

        Mapper<Clause.Persisted, org.openapitools.model.DslOutput> clauseModelDslMapper = new ClauseModelDslMapper(new ExpressionModelDslMapper(), errorMapper);
        ClauseDslModelMapper dslClauseMapper = new ClauseDslModelMapper();

        Mapper<org.openapitools.model.ClauseInput, Clause.NotPersisted> clauseInputMapper = new ClauseInputDtoModelMapper(new ExpressionDtoModelMapper());

        return new ManagementServiceAdaptor(
                managementService,
                clauseModelDtoMapper,
                dslClauseMapper,
                clauseModelDslMapper,
                clauseInputMapper
        );
    }

}