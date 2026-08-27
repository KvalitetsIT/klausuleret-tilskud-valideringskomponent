package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.DrugMedication;
import dk.kvalitetsit.itukt.common.repository.cache.CacheLoader;
import dk.kvalitetsit.itukt.common.service.DrugMedicationFormService;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DrugMedicationFormCache implements CacheLoader, DrugMedicationFormService {
    private final CacheConfiguration configuration;
    private final Repository<DrugMedication.Form> repository;
    private Set<DrugMedication.Form> forms = Set.of();

    public DrugMedicationFormCache(CacheConfiguration configuration, Repository<DrugMedication.Form> repository) {
        this.configuration = configuration;
        this.repository = repository;
    }

    @Override
    public String getCron() {
        return configuration.cron();
    }

    @Override
    public void load() {
        forms = repository.fetchAll().stream().collect(Collectors.toSet());
    }

    @Override
    public Set<DrugMedication.Form> getForms() {
        return forms;
    }

    @Override
    public Optional<DrugMedication.Form> getForm(String formCode) {
        return Optional.of(new DrugMedication.Form(formCode)).filter(forms::contains);
    }
}
