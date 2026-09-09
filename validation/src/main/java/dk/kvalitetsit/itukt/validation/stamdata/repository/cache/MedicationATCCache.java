package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.scheduled.ScheduledJob;
import dk.kvalitetsit.itukt.common.service.MedicationATCService;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;

import java.util.Optional;
import java.util.Set;

public class MedicationATCCache implements ScheduledJob, MedicationATCService {
    private final CacheConfiguration configuration;
    private final Repository<Medication.ATC> repository;
    private Set<Medication.ATC> atcs = Set.of();

    public MedicationATCCache(CacheConfiguration configuration, Repository<Medication.ATC> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void run() {
        atcs = Set.copyOf(repository.fetchAll());
    }

    @Override
    public Set<Medication.ATC> getATCs() {
        return atcs;
    }

    @Override
    public Optional<Medication.ATC> getATC(String atcCode) {
        return Optional.of(new Medication.ATC(atcCode)).filter(atcs::contains);
    }
}
