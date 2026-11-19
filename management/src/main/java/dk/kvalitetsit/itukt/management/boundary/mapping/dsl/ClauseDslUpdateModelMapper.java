package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParser;
import dk.kvalitetsit.itukt.management.exceptions.DslParserException;
import dk.kvalitetsit.itukt.management.service.model.ClauseUpdateInput;
import org.openapitools.model.DslUpdateInput;


public class ClauseDslUpdateModelMapper {
    private final DslParser dslParser;
    private final Mapper<org.openapitools.model.Expression, Expression> expressionDtoModelMapper;

    public ClauseDslUpdateModelMapper(DslParser dslParser, Mapper<org.openapitools.model.Expression, Expression> expressionDtoModelMapper) {
        this.dslParser = dslParser;
        this.expressionDtoModelMapper = expressionDtoModelMapper;
    }

    public ClauseUpdateInput map(DslUpdateInput dsl) throws DslParserException {
        var dtoExpression = dslParser.parse(dsl.getDsl());
        var modelExpression = expressionDtoModelMapper.map(dtoExpression);
        return new ClauseUpdateInput(modelExpression, dsl.getError());
    }
}
