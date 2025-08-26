package dk.kvalitetsit.itukt.management.configuration;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ExpressionModelDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.ClauseDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.ExpressionDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ExpressionModelDtoMapper;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ExpressionEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ClauseModelEntityMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ExpressionModelEntityMapper;
import dk.kvalitetsit.itukt.management.service.ManagementService;
import dk.kvalitetsit.itukt.management.service.ManagementServiceAdaptor;
import dk.kvalitetsit.itukt.management.service.ManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class ManagementBeanRegistration {

    private final ManagementConfiguration configuration;

    public ManagementBeanRegistration(ManagementConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public ClauseCache clauseCache() {
        // Hardcoded clause for phase 1
        Expression.Condition expression = new Expression.Condition("ALDER", Operator.GREATER_THAN, List.of("50"));
        Clause clause = new Clause("KRINI", Optional.of(UUID.randomUUID()), expression);
        return new ClauseCache(List.of(clause));
    }

    @Bean
    public ClauseRepository<ClauseEntity> clauseRepository(@Qualifier("appDataSource") DataSource dataSource){
        return new ClauseRepositoryImpl(dataSource);
    }

    @Bean
    public ClauseRepositoryAdaptor clauseRepositoryAdaptor(@Autowired ClauseRepository<ClauseEntity> clauseRepository) {
        return new ClauseRepositoryAdaptor(clauseRepository, new ClauseModelEntityMapper(new ExpressionModelEntityMapper()), new ClauseEntityModelMapper(new ExpressionEntityModelMapper()));
    }

    @Bean
    public ManagementService<Clause> managementService(@Autowired ClauseRepositoryAdaptor clauseRepository){
        return new ManagementServiceImpl(clauseRepository);
    }

    @Bean
    public ManagementServiceAdaptor managementServiceAdaptor(@Autowired ManagementService<Clause> managementService) {
        return new ManagementServiceAdaptor(
                managementService,
                new ClauseDtoModelMapper(new ExpressionDtoModelMapper()),
                new dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper(new ExpressionModelDtoMapper()),
                new ClauseDslModelMapper(),
                new ClauseModelDslMapper(new ExpressionModelDslMapper())
        );
    }

}