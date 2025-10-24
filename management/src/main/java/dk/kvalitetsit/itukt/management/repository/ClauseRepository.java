package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity.Persisted create(ClauseEntity.NotPersisted map);
    Optional<ClauseEntity.Persisted> read(UUID id);
    List<ClauseEntity.Persisted> readAll();
}
