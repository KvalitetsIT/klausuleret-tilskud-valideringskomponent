package dk.kvalitetsit.itukt.common.service;

import dk.kvalitetsit.itukt.common.model.DrugMedication;

import java.util.Optional;
import java.util.Set;

public interface DrugMedicationFormService {
    Set<DrugMedication.Form> getForms();
    Optional<DrugMedication.Form> getForm(String formCode);
}
