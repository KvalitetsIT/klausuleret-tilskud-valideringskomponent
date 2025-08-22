package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClauseCacheImpl implements ClauseCache {
    private final Map<String, Clause> nameToClauseMap;

    public ClauseCacheImpl(List<Clause> clauses) {
        this.nameToClauseMap = clauses.stream()
                .collect(Collectors.toMap(Clause::name, Function.identity()));
    }

    public Optional<Clause> getClause(String name) {
        return Optional.ofNullable(nameToClauseMap.get(name));
    }
}
