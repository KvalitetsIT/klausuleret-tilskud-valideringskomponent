package dk.kvalitetsit.itukt.management.repository.cache;

import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;

import java.util.Optional;

public interface ClauseCache {
    /**
     * @param name the name of the clause you want to retrieve from the cache
     * @return the clause corresponding to that name if present otherwise Optional.empty
     */
    Optional<ClauseEntity.Persisted> get(String name);

    Optional<ClauseEntity.Persisted> getByErrorCode(Integer errorCode);
}
