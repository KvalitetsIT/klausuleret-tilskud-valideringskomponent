package dk.kvalitetsit.itukt.common.repository.cache;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.Optional;

public interface ClauseCache extends CacheLoader {
    Optional<Clause> get(String name);
}
