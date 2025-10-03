package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepositoryAdaptor;
import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StamdataCacheImpl implements StamdataCache, CacheLoader {

    private final CacheConfiguration configuration;
    private final StamDataRepositoryAdaptor concreteStamDataRepository;
    private Map<Long, StamData> drugIdToClauseMap = new HashMap<>();

    public StamdataCacheImpl(CacheConfiguration configuration, StamDataRepositoryAdaptor concreteStamDataRepository) {
        this.configuration = configuration;
        this.concreteStamDataRepository = concreteStamDataRepository;
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        drugIdToClauseMap = concreteStamDataRepository.findAll();
    }

    @Override
    public Optional<StamData> get(Long id) {
        return Optional.ofNullable(this.drugIdToClauseMap.get(id));
    }
}
