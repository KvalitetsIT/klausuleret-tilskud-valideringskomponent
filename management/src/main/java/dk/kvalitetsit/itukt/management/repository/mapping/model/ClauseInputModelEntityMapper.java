package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseFullInput;

public class ClauseInputModelEntityMapper implements Mapper<ClauseFullInput, ClauseEntityInput> {
    private final Mapper<Expression, ExpressionEntity> expressionMapper;

    public ClauseInputModelEntityMapper(Mapper<Expression, ExpressionEntity> expressionMapper) {
        this.expressionMapper = expressionMapper;
    }

    @Override
    public ClauseEntityInput map(ClauseFullInput clauseInput) {
        var expressionEntity = expressionMapper.map(clauseInput.expression());
        return new ClauseEntityInput(
                clauseInput.name(),
                expressionEntity,
                clauseInput.errorMessage(),
                clauseInput.status(),
                clauseInput.validFrom());
    }
}
