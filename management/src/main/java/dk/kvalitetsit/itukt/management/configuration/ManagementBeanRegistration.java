package dk.kvalitetsit.itukt.management.configuration;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslDtoMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDtoDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.Lexer;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.ExpressionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.TokenParserFactory;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.ConditionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.MultiValueTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.StructuredValueTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder.*;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl.ExpressionDtoDslMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl.MapperFactory;
import dk.kvalitetsit.itukt.management.boundary.mapping.dto.ExpressionDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseInputDtoModelMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.model.ExpressionModelDtoMapper;
import dk.kvalitetsit.itukt.management.repository.*;
import dk.kvalitetsit.itukt.management.repository.cache.ActiveClauseCache;
import dk.kvalitetsit.itukt.management.repository.cache.ActiveClauseCacheImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ClauseEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.entity.ExpressionEntityModelMapper;
import dk.kvalitetsit.itukt.management.repository.mapping.model.ExpressionModelEntityMapper;
import dk.kvalitetsit.itukt.management.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class ManagementBeanRegistration {

    private final ManagementConfiguration configuration;

    public ManagementBeanRegistration(ManagementConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    @RequestScope
    public UserContextService userContextService(HttpServletRequest request) {
        return new UserContextService(request);
    }

    @Bean
    public ActiveClauseCache activeClauseCache(ClauseRepository clauseRepository) {
        return new ActiveClauseCacheImpl(configuration.clause().cache(), clauseRepository);
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
    public ClauseService clauseService(ActiveClauseCache cache, Mapper<ClauseEntity, Clause> mapper) {
        return new ClauseServiceImpl(cache, mapper);
    }

    @Bean
    public Mapper<ClauseEntity, Clause> clauseEntityModelMapper() {
        return new ClauseEntityModelMapper(new ExpressionEntityModelMapper());
    }

    @Bean
    public ClauseRepositoryAdaptor clauseRepositoryAdaptor(@Autowired ClauseRepository clauseRepository, Mapper<ClauseEntity, Clause> mapper) {
        return new ClauseRepositoryAdaptor(clauseRepository, mapper, new ExpressionModelEntityMapper());
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
    public DslParser dslParser() {
        List<ConditionBuilder> conditionBuilders = List.of(
                new AgeConditionBuilder(),
                new IndicationConditionBuilder(),
                new DoctorSpecialityConditionBuilder(),
                new DepartmentSpecialityConditionBuilder(),
                new ExistingDrugMedicationConditionBuilder()
        );
        var structuredValueTokenParser = new StructuredValueTokenParser();
        var multiValueTokenParser = new MultiValueTokenParser(structuredValueTokenParser);
        var conditionTokenParser = new ConditionTokenParser(multiValueTokenParser, structuredValueTokenParser);
        var tokenParserFactory = new TokenParserFactory(conditionTokenParser, conditionBuilders);
        var expressionTokenParser = new ExpressionTokenParser(tokenParserFactory);
        return new DslParser(expressionTokenParser, new Lexer());
    }

    @Bean
    public ManagementServiceAdaptor managementServiceAdaptor(
            @Autowired ManagementService managementService,
            @Autowired MapperFactory mapperFactory,
            @Autowired DslParser dslParser) {
        return new ManagementServiceAdaptor(
                managementService,
                new dk.kvalitetsit.itukt.management.boundary.mapping.model.ClauseModelDtoMapper(
                        new ExpressionModelDtoMapper()
                ),
                new ClauseDslDtoMapper(dslParser),
                new ClauseDtoDslMapper(new ExpressionDtoDslMapper(mapperFactory)),
                new ClauseInputDtoModelMapper(new ExpressionDtoModelMapper())
        );
    }

}