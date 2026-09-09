package dk.kvalitetsit.itukt.common.service;

import dk.kvalitetsit.itukt.common.model.Medication;

import java.util.Optional;
import java.util.Set;

public interface MedicationATCService {
    Set<Medication.ATC> getATCs();
    Optional<Medication.ATC> getATC(String atcCode);
}
