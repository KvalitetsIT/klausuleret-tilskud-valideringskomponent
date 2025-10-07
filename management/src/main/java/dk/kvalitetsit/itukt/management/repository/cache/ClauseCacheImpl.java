package dk.kvalitetsit.itukt.management.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClauseCacheImpl implements ClauseCache, CacheLoader {

    private final CacheConfiguration configuration;
    private final ClauseRepository clauseRepository;
    private Map<String, ClauseEntity> nameToClauseMap = new HashMap<>();
    private Map<Integer, ClauseEntity> errorCodeToClauseMap = new HashMap<>();

    public ClauseCacheImpl(CacheConfiguration configuration, ClauseRepository clauseRepository) {
        this.configuration = configuration;
        this.clauseRepository = clauseRepository;
    }

    @Override
    public Optional<ClauseEntity> get(String name) {
        return Optional.ofNullable(nameToClauseMap.get(name));
    }

    @Override
    public Optional<ClauseEntity> getByErrorCode(Integer errorCode) {
        return Optional.ofNullable(errorCodeToClauseMap.get(errorCode));
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        var clauses = clauseRepository.readAll();
        nameToClauseMap = clauses.stream().collect(Collectors.toMap(ClauseEntity::name, Function.identity()));
        errorCodeToClauseMap = clauses.stream().collect(Collectors.toMap(ClauseEntity::errorCode, Function.identity()));
    }
}
