package dk.kvalitetsit.itukt.common.service;

import dk.kvalitetsit.itukt.common.model.Medication;

import java.util.Optional;
import java.util.Set;

public interface MedicationFormService {
    Set<Medication.Form> getForms();
    Optional<Medication.Form> getForm(String formCode);
}
