package dk.kvalitetsit.itukt.management.configuration;

import dk.kvalitetsit.itukt.common.repository.cache.ClauseCache;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ExpressionModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.ExpressionDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseInputDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ExpressionModelDtoMapper;
import dk.kvalitetsit.itukt.management.repository.ClauseCacheImpl;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ExpressionEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ExpressionModelEntityMapper;
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
    public ClauseCache clauseCache(ClauseRepositoryAdaptor clauseRepository) {
        return new ClauseCacheImpl(configuration.clause().cache(), clauseRepository);
    }

    @Bean
    public ClauseRepository clauseRepository(@Qualifier("appDataSource") DataSource dataSource) {
        return new ClauseRepositoryImpl(dataSource);
    }

    @Bean
    public ClauseRepositoryAdaptor clauseRepositoryAdaptor(@Autowired ClauseRepository clauseRepository) {
        return new ClauseRepositoryAdaptor(clauseRepository, new ClauseEntityModelMapper(new ExpressionEntityModelMapper()));
    }

    @Bean
    public ManagementService managementService(@Autowired ClauseRepositoryAdaptor clauseRepository) {
        return new ManagementServiceImpl(clauseRepository);
    }

    @Bean
    public ManagementServiceAdaptor managementServiceAdaptor(@Autowired ManagementService managementService) {
        return new ManagementServiceAdaptor(
                managementService,
                new dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper(new ExpressionModelDtoMapper()),
                new ClauseDslModelMapper(),
                new ClauseModelDslMapper(new ExpressionModelDslMapper()),
                new ClauseInputDtoModelMapper(new ExpressionDtoModelMapper(), new ExpressionModelEntityMapper())
        );
    }

}