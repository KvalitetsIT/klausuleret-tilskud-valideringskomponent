package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

import java.util.Optional;

public interface ExpressionRepository {
    ExpressionEntity.Persisted create(ExpressionEntity.NotPersisted expression);
    Optional<ExpressionEntity.Persisted> read(long expressionId);
}
