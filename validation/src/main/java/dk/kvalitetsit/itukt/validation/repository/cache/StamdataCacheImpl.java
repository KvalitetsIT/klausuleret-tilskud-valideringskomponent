package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepositoryAdaptor;
import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StamdataCacheImpl implements Cache<StamData>, CacheLoader {

    private final CacheConfiguration configuration;
    private final StamDataRepositoryAdaptor concreteStamDataRepository;
    private Map<Long, StamData> drugIdToClauseMap = new HashMap<>();

    public StamdataCacheImpl(CacheConfiguration configuration, StamDataRepositoryAdaptor concreteStamDataRepository) {
        this.configuration = configuration;
        this.concreteStamDataRepository = concreteStamDataRepository;
    }

    private static StamData merge(StamData x, StamData y) {
        var clauses = Stream.concat(x.clauses().stream(), y.clauses().stream()).collect(Collectors.toSet());
        return new StamData(x.drug(), clauses);
    }

    private static Map<Long, StamData> toMap(List<StamData> entry) {
        return entry.stream()
                .distinct()
                .collect(Collectors.toMap(
                        x -> x.drug().id(),
                        Function.identity(),
                        StamdataCacheImpl::merge
                ));
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        drugIdToClauseMap = toMap(concreteStamDataRepository.findAll());
    }

    @Override
    public Optional<StamData> get(Long id) {
        return Optional.ofNullable(this.drugIdToClauseMap.get(id));
    }
}