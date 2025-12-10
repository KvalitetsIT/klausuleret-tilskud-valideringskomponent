package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.validation.repository.DrugClauseViewRepositoryAdaptor;
import dk.kvalitetsit.itukt.validation.service.model.DrugClause;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DrugClauseCacheImpl implements DrugClauseCache, CacheLoader {

    private final CacheConfiguration configuration;
    private final DrugClauseViewRepositoryAdaptor concreteStamDataRepository;
    private Map<Long, DrugClause> drugIdToClauseMap = new HashMap<>();

    public DrugClauseCacheImpl(CacheConfiguration configuration, DrugClauseViewRepositoryAdaptor concreteStamDataRepository) {
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
    public Optional<DrugClause> get(Long id) {
        return Optional.ofNullable(this.drugIdToClauseMap.get(id));
    }
}
