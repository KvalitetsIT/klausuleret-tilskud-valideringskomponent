package dk.kvalitetsit.itukt.common.model;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;

sealed public interface ValidationError extends ValidationFailed permits AndError, ConditionError, ExistingDrugMedicationError, OrError {
    record ConditionError(Field field, Operator operator, String value) implements ValidationError {
        public enum Field {AGE, INDICATION, DOCTOR_SPECIALITY, DEPARTMENT_SPECIALITY}
    }

    record ExistingDrugMedicationError(ExistingDrugMedication existingDrugMedication) implements ValidationError {
    }

    record AndError(ValidationError e1, ValidationError e2) implements ValidationError {
    }

    record OrError(ValidationError e1, ValidationError e2) implements ValidationError {
    }
}