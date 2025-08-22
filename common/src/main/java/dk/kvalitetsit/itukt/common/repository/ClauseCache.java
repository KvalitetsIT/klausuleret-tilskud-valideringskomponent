package dk.kvalitetsit.itukt.common.repository;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.Optional;

public interface ClauseCache {
    Optional<Clause> getClause(String name);
}
