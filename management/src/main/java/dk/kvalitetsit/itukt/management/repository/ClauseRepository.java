package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.repository.core.Creatable;
import dk.kvalitetsit.itukt.common.repository.core.Readable;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;

import java.util.UUID;

public interface ClauseRepository extends Creatable<ClauseEntity, ClauseEntity.Persisted, ClauseEntity.NotPersisted>, Readable<ClauseEntity, ClauseEntity.Persisted, UUID> {

}
