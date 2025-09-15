package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.cache.ClauseCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClauseCacheImpl implements ClauseCache {

    private final CacheConfiguration configuration;
    private final ClauseRepository<Clause> clauseRepository;
    private Map<String, Clause> clauses = new HashMap<>();

    public ClauseCacheImpl(CacheConfiguration configuration, ClauseRepository<Clause> clauseRepository) {
        this.configuration = configuration;
        this.clauseRepository = clauseRepository;
    }

    @Override
    public Optional<Clause> get(String name) {
        return Optional.ofNullable(clauses.get(name));
    }

    @Override
    public String get_cron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        this.clauses = clauseRepository.readAll().stream().collect(Collectors.toMap(Clause::name, x -> x));
    }
}
