package dk.kvalitetsit.itukt.repository.mapping.model;

import dk.kvalitetsit.itukt.Mapper;
import dk.kvalitetsit.itukt.model.Clause;
import dk.kvalitetsit.itukt.repository.model.ClauseEntity;
import dk.kvalitetsit.itukt.model.Expression;
import dk.kvalitetsit.itukt.repository.model.ExpressionEntity;

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
