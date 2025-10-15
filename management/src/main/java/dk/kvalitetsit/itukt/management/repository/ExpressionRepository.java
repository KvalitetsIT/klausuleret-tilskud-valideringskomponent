package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.repository.core.Creatable;
import dk.kvalitetsit.itukt.common.repository.core.Readable;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public interface ExpressionRepository extends Creatable<ExpressionEntity, ExpressionEntity.Persisted, ExpressionEntity.NotPersisted>, Readable<ExpressionEntity, ExpressionEntity.Persisted, Long> {
}
