package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
import org.openapitools.model.ClauseInput;

public class ClauseInputDtoModelMapper implements Mapper<ClauseInput, ClauseForCreation> {
    private final Mapper<org.openapitools.model.Expression, Expression> expressionDtoModelMapper;
    private final Mapper<Expression, ExpressionEntity> expressionModelEntityMapper;

    public ClauseInputDtoModelMapper(
            Mapper<org.openapitools.model.Expression, Expression> expressionDtoModelMapper,
            Mapper<Expression, ExpressionEntity> expressionModelEntityMapper
    ) {
        this.expressionDtoModelMapper = expressionDtoModelMapper;
        this.expressionModelEntityMapper = expressionModelEntityMapper;
    }

    @Override
    public ClauseForCreation map(ClauseInput clauseInput) {
        var expression = expressionDtoModelMapper.map(clauseInput.getExpression());
        var expressionEntity = expressionModelEntityMapper.map(expression);
        return new ClauseForCreation(clauseInput.getName(), expressionEntity, clauseInput.getError().getMessage());
    }
}
