package dk.kvalitetsit.itukt.management.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClauseCacheImpl implements ClauseCache, CacheLoader {

    private final CacheConfiguration configuration;
    private final ClauseRepository clauseRepository;
    private Map<String, ClauseEntity> clauses = new HashMap<>();

    public ClauseCacheImpl(CacheConfiguration configuration, ClauseRepository clauseRepository) {
        this.configuration = configuration;
        this.clauseRepository = clauseRepository;
    }

    @Override
    public Optional<ClauseEntity> get(String name) {
        return Optional.ofNullable(clauses.get(name));
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        this.clauses = clauseRepository.readAll().stream().collect(Collectors.toMap(ClauseEntity::name, x -> x));
    }
}
