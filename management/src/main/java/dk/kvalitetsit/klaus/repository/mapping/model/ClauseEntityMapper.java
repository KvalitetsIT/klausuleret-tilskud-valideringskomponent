package dk.kvalitetsit.klaus.repository.mapping.model;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.repository.model.ClauseEntity;
import dk.kvalitetsit.klaus.repository.model.ExpressionEntity;

public class ClauseEntityMapper implements Mapper<Clause, ClauseEntity> {

    private final Mapper<Expression, ExpressionEntity> expressionEntityMapper;

    public ClauseEntityMapper(Mapper<Expression, ExpressionEntity> expressionEntityMapper) {
        this.expressionEntityMapper = expressionEntityMapper;
    }

    @Override
    public ClauseEntity map(Clause entry) {
        return new ClauseEntity(null, null, entry.name(), this.expressionEntityMapper.map(entry.expression()));
    }
}
