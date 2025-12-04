package dk.kvalitetsit.itukt.common.model;

import static dk.kvalitetsit.itukt.common.model.ValidationFailed.*;

public sealed interface ValidationFailed permits ValidationError, ExistingDrugMedicationRequired {
    record ExistingDrugMedicationRequired() implements ValidationFailed {}
}