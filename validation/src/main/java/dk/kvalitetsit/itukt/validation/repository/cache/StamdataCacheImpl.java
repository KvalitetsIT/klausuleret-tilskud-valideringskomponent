package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.validation.repository.Repository;
import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StamdataCacheImpl implements Cache<Long, StamData>, CacheLoader {

    private final CacheConfiguration configuration;
    private final Repository<StamData> repository;
    private volatile Map<Long, StamData> entries = Map.of();

    public StamdataCacheImpl(CacheConfiguration configuration, Repository<StamData> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    private static StamData resolveConflict(StamData x, StamData y) {
        var clauses = Stream.concat(x.clauses().stream(), y.clauses().stream()).collect(Collectors.toSet());
        return new StamData(x.drug(), clauses);
    }

    private <T> Map<T, StamData> toMap(List<StamData> entry, Function<StamData, T> identity) {
        return entry.stream().collect(Collectors.toMap(
                identity,
                Function.identity(),
                StamdataCacheImpl::resolveConflict
        ));
    }

    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        var response = repository.findAll();
        entries = toMap(response, (d) -> d.drug().id());
    }

    public Optional<StamData> get(Long id) {
        return Optional.ofNullable(this.entries.get(id));
    }
}
