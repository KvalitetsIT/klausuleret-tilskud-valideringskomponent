package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.validation.repository.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Cache<T, ID> implements CacheLoader {

    private final CacheConfiguration configuration;
    private final Repository<T> repository;
    private volatile Map<ID, T> entries = Map.of();

    public Cache(CacheConfiguration configuration, Repository<T> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    abstract T resolveConflict(T x, T y);

    private Map<ID, T> toMap(List<T> entry) {
        return entry.stream().collect(Collectors.toMap(this::getIdentifier, Function.identity(), this::resolveConflict));
    }

    abstract ID getIdentifier(T item);

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        entries = toMap(repository.findAll());
    }

    public Optional<T> get(ID id) {
        return Optional.ofNullable(this.entries.get(id));
    }
}