package dk.kvalitetsit.itukt.management.configuration;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.ClauseDslDtoMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.ClauseDtoDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression.ExpressionDtoDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression.MapperFactory;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.ExpressionDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseInputDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ExpressionModelDtoMapper;
import dk.kvalitetsit.itukt.management.repository.*;
import dk.kvalitetsit.itukt.management.repository.cache.ClauseCache;
import dk.kvalitetsit.itukt.management.repository.cache.ClauseCacheImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ExpressionEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ExpressionModelEntityMapper;
import dk.kvalitetsit.itukt.management.service.ClauseServiceImpl;
import dk.kvalitetsit.itukt.management.service.ManagementService;
import dk.kvalitetsit.itukt.management.service.ManagementServiceAdaptor;
import dk.kvalitetsit.itukt.management.service.ManagementServiceImpl;
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
    public ClauseService clauseService(ClauseCache cache, Mapper<ClauseEntity, Clause> mapper) {
        return new ClauseServiceImpl(cache, mapper);
    }

    @Bean
    public Mapper<ClauseEntity, Clause> clauseEntityModelMapper() {
        return new ClauseEntityModelMapper(new ExpressionEntityModelMapper());
    }

    @Bean
    public ClauseRepositoryAdaptor clauseRepositoryAdaptor(@Autowired ClauseRepository clauseRepository, Mapper<ClauseEntity, Clause> mapper) {
        return new ClauseRepositoryAdaptor(clauseRepository, mapper);
    }

    @Bean
    public ManagementService managementService(@Autowired ClauseRepositoryAdaptor clauseRepository) {
        return new ManagementServiceImpl(clauseRepository);
    }

    @Bean
    public MapperFactory mapperFactory() {
        return new MapperFactory();
    }

    @Bean
    public ManagementServiceAdaptor managementServiceAdaptor(@Autowired ManagementService managementService, @Autowired MapperFactory mapperFactory) {
        return new ManagementServiceAdaptor(
                managementService,
                new dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper(
                        new ExpressionModelDtoMapper()
                ),
                new ClauseDslDtoMapper(),
                new ClauseDtoDslMapper(new ExpressionDtoDslMapper(mapperFactory)),
                new ClauseInputDtoModelMapper(new ExpressionDtoModelMapper(), new ExpressionModelEntityMapper())
        );
    }

}