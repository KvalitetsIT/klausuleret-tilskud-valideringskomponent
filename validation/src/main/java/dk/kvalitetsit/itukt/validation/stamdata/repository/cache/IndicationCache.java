package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Indication;
import dk.kvalitetsit.itukt.common.scheduled.ScheduledJob;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;

import java.util.Optional;
import java.util.Set;

public class IndicationCache implements ScheduledJob, StamdataCacheService<Indication> {
    private final CacheConfiguration configuration;
    private final Repository<Indication> repository;
    private Set<Indication> indications = Set.of();

    public IndicationCache(CacheConfiguration configuration, Repository<Indication> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void run() {
        indications = Set.copyOf(repository.fetchAll());
    }

    @Override
    public Set<Indication> getAll() {
        return indications;
    }

    @Override
    public Optional<Indication> get(String indicationCode) {
        return indications.stream()
                .filter(indication -> indicationCode.equals(String.valueOf(indication.code())))
                .findFirst();
    }
}
