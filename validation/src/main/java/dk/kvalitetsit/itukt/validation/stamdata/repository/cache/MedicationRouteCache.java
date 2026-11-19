package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.scheduled.ScheduledJob;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;

import java.util.Optional;
import java.util.Set;

public class MedicationRouteCache implements ScheduledJob, StamdataCacheService<Medication.Route> {
    private final CacheConfiguration configuration;
    private final Repository<Medication.Route> repository;
    private Set<Medication.Route> routes = Set.of();

    public MedicationRouteCache(CacheConfiguration configuration, Repository<Medication.Route> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void run() {
        routes = Set.copyOf(repository.fetchAll());
    }

    @Override
    public Set<Medication.Route> getAll() {
        return routes;
    }

    @Override
    public Optional<Medication.Route> get(String routeCode) {
        return Optional.of(new Medication.Route(routeCode)).filter(routes::contains);
    }
}
