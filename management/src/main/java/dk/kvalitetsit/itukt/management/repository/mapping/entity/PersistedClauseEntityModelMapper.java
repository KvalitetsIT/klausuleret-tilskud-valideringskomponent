package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Error;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class PersistedClauseEntityModelMapper implements Mapper<ClauseEntity.Persisted, Clause.Persisted> {
    private final Mapper<ExpressionEntity.Persisted, Expression.Persisted> expressionEntityModelMapper;

    public PersistedClauseEntityModelMapper(Mapper<ExpressionEntity.Persisted, Expression.Persisted> expressionEntityModelMapper) {
        this.expressionEntityModelMapper = expressionEntityModelMapper;
    }

    @Override
    public Clause.Persisted map(ClauseEntity.Persisted entry) {
        return new Clause.Persisted(entry.id(), entry.uuid(), entry.name(), new Error(entry.errorMessage(), entry.errorCode()), expressionEntityModelMapper.map(entry.expression()));
    }
}
