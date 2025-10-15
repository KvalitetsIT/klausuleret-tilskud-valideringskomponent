package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class NotPersistedClauseModelEntityMapper implements Mapper<Clause.NotPersisted, ClauseEntity.NotPersisted> {
    private final Mapper<Expression.NotPersisted, ExpressionEntity.NotPersisted> expressionEntityModelMapper;

    public NotPersistedClauseModelEntityMapper(Mapper<Expression.NotPersisted, ExpressionEntity.NotPersisted> expressionEntityModelMapper) {
        this.expressionEntityModelMapper = expressionEntityModelMapper;
    }


    @Override
    public ClauseEntity.NotPersisted map(Clause.NotPersisted entry) {
        return new ClauseEntity.NotPersisted(entry.name(), expressionEntityModelMapper.map(entry.expression()), entry.errorMessage());
    }
}
