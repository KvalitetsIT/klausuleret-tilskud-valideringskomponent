package dk.kvalitetsit.itukt.common.model;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;
import static java.lang.String.join;

sealed public interface ValidationError extends ValidationFailed permits AndError, ConditionError, ExistingDrugMedicationError, OrError {
    String toErrorString();

    record ConditionError(Field field, Operator operator, String value) implements ValidationError {
        private static String toErrorString(Operator operator) {
            return switch (operator) {
                case EQUAL -> "skal være";
                case GREATER_THAN_OR_EQUAL_TO -> "skal være større end eller lig";
                case LESS_THAN_OR_EQUAL_TO -> "skal være mindre end eller lig";
                case GREATER_THAN -> "skal være større end";
                case LESS_THAN -> "skal være mindre end";
            };
        }

        private static String toErrorString(Field field) {
            return switch (field) {
                case AGE -> "alder";
                case INDICATION -> "indikation";
                case DOCTOR_SPECIALITY -> "lægespeciale";
                case DEPARTMENT_SPECIALITY -> "afdelingens speciale";
            };
        }

        public String toErrorString() {
            return join(" ",
                    toErrorString(field),
                    toErrorString(operator),
                    value
            );
        }

        public enum Field {AGE, INDICATION, DOCTOR_SPECIALITY, DEPARTMENT_SPECIALITY}
    }

    record ExistingDrugMedicationError(ExistingDrugMedication existingDrugMedication) implements ValidationError {
        @Override
        public String toErrorString() {
            return "tidligere medicinsk behandling med følgende påkrævet:" +
                    " ATC = " + existingDrugMedication.atcCode() +
                    ", Formkode = " + existingDrugMedication.formCode() +
                    ", Administrationsrutekode = " + existingDrugMedication.routeOfAdministrationCode();
        }
    }

    record AndError(ValidationError e1, ValidationError e2) implements ValidationError {
        private static String parenthesesIfNecessary(ValidationError error) {
            return error instanceof OrError ? "(" + error.toErrorString() + ")" : error.toErrorString();
        }

        @Override
        public String toErrorString() {
            return parenthesesIfNecessary(e1) + " og " + parenthesesIfNecessary(e2);
        }
    }

    record OrError(ValidationError e1, ValidationError e2) implements ValidationError {
        @Override
        public String toErrorString() {
            return e1.toErrorString() + " eller " + e2.toErrorString();
        }
    }
}