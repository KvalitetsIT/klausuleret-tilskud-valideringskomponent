package dk.kvalitetsit.itukt.common.model;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;
import static java.lang.String.join;

sealed public interface ValidationError extends ValidationFailed permits AndError, ConditionError, ExistingDrugMedicationError, OrError {
    String toErrorString();
    enum Field {AGE, INDICATION, DOCTOR_SPECIALITY}

    record ConditionError(Field field, Operator operator, String value) implements ValidationError {
        private static String toErrorString(Operator field) {
            return switch (field) {
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
            };
        }

        public String toErrorString() {
            return join(" ",
                    toErrorString(field),
                    toErrorString(operator),
                    value
            );
        }
    }
    record ExistingDrugMedicationError(String atcCode, String formCode, String routeOfAdministrationCode) implements ValidationError {
        @Override
        public String toErrorString() {
            return "Tidligere medicinsk behandling med følgende påkrævet:" +
                    " ATC = " + atcCode +
                    ", Formkode = " + formCode +
                    ", Administrationsrutekode = " + routeOfAdministrationCode;
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