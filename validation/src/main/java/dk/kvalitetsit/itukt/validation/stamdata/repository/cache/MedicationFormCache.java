package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.scheduled.ScheduledJob;
import dk.kvalitetsit.itukt.common.service.MedicationFormService;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;

import java.util.Optional;
import java.util.Set;

public class MedicationFormCache implements ScheduledJob, MedicationFormService {
    private final CacheConfiguration configuration;
    private final Repository<Medication.Form> repository;
    private Set<Medication.Form> forms = Set.of();

    public MedicationFormCache(CacheConfiguration configuration, Repository<Medication.Form> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void run() {
        forms = Set.copyOf(repository.fetchAll());
    }

    @Override
    public Set<Medication.Form> getForms() {
        return forms;
    }

    @Override
    public Optional<Medication.Form> getForm(String formCode) {
        return Optional.of(new Medication.Form(formCode)).filter(forms::contains);
    }
}
