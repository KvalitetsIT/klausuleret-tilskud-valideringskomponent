package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.DoctorSpeciality;
import dk.kvalitetsit.itukt.common.scheduled.ScheduledJob;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;

import java.util.Optional;
import java.util.Set;

public class DoctorSpecialityCache implements ScheduledJob, StamdataCacheService<DoctorSpeciality> {
    private final CacheConfiguration configuration;
    private final Repository<DoctorSpeciality> repository;
    private Set<DoctorSpeciality> specialities = Set.of();

    public DoctorSpecialityCache(CacheConfiguration configuration, Repository<DoctorSpeciality> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void run() {
        specialities = Set.copyOf(repository.fetchAll());
    }

    @Override
    public Set<DoctorSpeciality> getAll() {
        return specialities;
    }

    @Override
    public Optional<DoctorSpeciality> get(String speciality) {
        return Optional.of(new DoctorSpeciality(speciality)).filter(specialities::contains);
    }
}
