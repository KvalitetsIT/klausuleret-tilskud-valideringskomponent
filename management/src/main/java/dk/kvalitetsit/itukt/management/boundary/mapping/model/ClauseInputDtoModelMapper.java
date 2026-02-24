package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

public class ClauseInputDtoModelMapper implements Mapper<org.openapitools.model.ClauseInput, ClauseInput> {
    private final Mapper<org.openapitools.model.Expression, Expression> expressionDtoModelMapper;

    public ClauseInputDtoModelMapper(
            Mapper<org.openapitools.model.Expression, Expression> expressionDtoModelMapper
    ) {
        this.expressionDtoModelMapper = expressionDtoModelMapper;
    }

    @Override
    public ClauseInput map(org.openapitools.model.ClauseInput clauseInput) {
        var expression = expressionDtoModelMapper.map(clauseInput.getExpression());
        return new ClauseInput(clauseInput.getName(), expression, clauseInput.getError());
    }
}
