package dk.kvalitetsit.itukt.common.repository;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClauseCache {
    private final Map<String, Clause> nameToClauseMap;

    public ClauseCache(List<Clause> clauses) {
        this.nameToClauseMap = clauses.stream()
                .collect(Collectors.toMap(Clause::name, Function.identity()));
    }

    public Optional<Clause> getClause(String name) {
        return Optional.ofNullable(nameToClauseMap.get(name));
    }
}
