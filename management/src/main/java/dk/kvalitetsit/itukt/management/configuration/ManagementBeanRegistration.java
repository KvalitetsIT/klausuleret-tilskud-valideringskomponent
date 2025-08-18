package dk.kvalitetsit.itukt.management.configuration;

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
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryMock;
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
    public ClauseRepository<ClauseEntity> clauseRepository(@Qualifier("appDataSource") DataSource dataSource){
        if (configuration.isMocked()) return new ClauseRepositoryMock();
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
    public ManagementServiceAdaptor managementServiceAdaptor(@Autowired ManagementService<Clause> managementService) {
        return new ManagementServiceAdaptor(
                managementService,
                new DtoClauseMapper(new DtoExpressionMapper()),
                new ClauseModelDtoMapper(new ExpressionModelDtoMapper()),
                new DslClauseMapper(),
                new ClauseDslMapper(new ExpressionDslMapper())
        );
    }

}