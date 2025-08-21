package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class ClauseEntityMapper implements Mapper<Clause, ClauseEntity> {

    private final Mapper<Expression, ExpressionEntity> expressionEntityMapper;

    public ClauseEntityMapper(Mapper<Expression, ExpressionEntity> expressionEntityMapper) {
        this.expressionEntityMapper = expressionEntityMapper;
    }

    @Override
    public ClauseEntity map(Clause entry) {
        return new ClauseEntity(
                null,
                entry.uuid().orElse(null),
                entry.name(),
                this.expressionEntityMapper.map(entry.expression())
        );
    }
}
