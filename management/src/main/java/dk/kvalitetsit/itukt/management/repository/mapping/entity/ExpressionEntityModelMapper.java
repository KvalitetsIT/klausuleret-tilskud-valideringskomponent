package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class ExpressionEntityModelMapper implements Mapper<ExpressionEntity, Expression> {

    private final Mapper<ExpressionEntity.Persisted, Expression.Persisted> peristedExpressionMapper = new PersistedExpressionEntityModelMapper();
    private final Mapper<ExpressionEntity.NotPersisted, Expression.NotPersisted> notPeristedExpressionMapper = new NotPersistedExpressionEntityModelMapper();

    @Override
    public Expression map(ExpressionEntity entry) {
        return switch (entry) {
            case ExpressionEntity.NotPersisted notPersisted -> notPeristedExpressionMapper.map(notPersisted);
            case ExpressionEntity.Persisted persisted -> peristedExpressionMapper.map(persisted);
        };
    }


}
