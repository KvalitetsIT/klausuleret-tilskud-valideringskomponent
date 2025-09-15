package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.StamData;
import dk.kvalitetsit.itukt.common.repository.cache.StamdataCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StamdataCacheImpl implements StamdataCache {

    private final CacheConfiguration configuration;
    private final StamDataRepository concreteStamDataRepository;
    private Map<Long, StamData> drugIdToClauseMap = new HashMap<>();

    public StamdataCacheImpl(CacheConfiguration configuration, StamDataRepository concreteStamDataRepository) {
        this.configuration = configuration;
        this.concreteStamDataRepository = concreteStamDataRepository;
    }

    @Override
    public String get_cron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        var result = concreteStamDataRepository.findAll();
        drugIdToClauseMap = result.stream().collect(Collectors.toMap(x -> x.drug().id(), x -> x, (x, y) -> x));
    }

    @Override
    public Optional<StamData> get(Long id) {
        return Optional.ofNullable(this.drugIdToClauseMap.get(id));
    }
}
