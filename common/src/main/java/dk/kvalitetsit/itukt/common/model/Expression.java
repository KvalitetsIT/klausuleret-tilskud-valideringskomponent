package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static java.lang.String.join;

public sealed interface Expression permits Expression.Condition, BinaryExpression {
    Optional<ValidationError> validates(ValidationInput validationInput);

    sealed interface Condition extends Expression permits IndicationConditionExpression, AgeConditionExpression, ExistingDrugMedicationConditionExpression {
    }

    sealed interface ValidationError permits AndError, ExistingDrugMedicationError, OrError, ConditionError {
        default String errorMessage() {
            return switch (this) {
                case AndError ignored -> toErrorString(this);
                case ConditionError ignored -> toErrorString(this);
                case OrError orError -> toErrorString(orError.e1) + " eller " + toErrorString(orError.e2);
                case ExistingDrugMedicationError existingDrugMedicationError -> toErrorString(existingDrugMedicationError);
            };
        }

        enum Field { AGE, INDICATION }

        static String toErrorString(ValidationError e) {
            return switch (e) {
                case AndError(var e1, var e2) -> toErrorString(e1) + " og " + toErrorString(e2);
                case OrError(var e1, var e2) -> "(" + toErrorString(e1) + " eller " + toErrorString(e2) + ")";
                case ConditionError conditionError -> toErrorString(conditionError);
                case ExistingDrugMedicationError(var atcCode, var formCode, var routeOfAdministrationCode) ->
                        "Tidligere medicinsk behandling med følgende påkrævet:" +
                        " ATC = " + atcCode +
                        ", Formkode = " + formCode +
                        ", Administrationsrutekode = " + routeOfAdministrationCode;
            };
        }

        static String toErrorString(ConditionError error) {
            return join(" ",
                    toErrorString(error.field()),
                    toErrorString(error.operator()),
                    error.value());
        }

        static String toErrorString(Field field) {
            return switch (field) {
                case AGE -> "alder";
                case INDICATION -> "indikation";
            };
        }

        static String toErrorString(Operator field) {
            return switch (field) {
                case EQUAL -> "skal være";
                case GREATER_THAN_OR_EQUAL_TO -> "skal være større end eller lig";
                case LESS_THAN_OR_EQUAL_TO -> "skal være mindre end eller lig";
                case GREATER_THAN -> "skal være større end";
                case LESS_THAN -> "skal være mindre end";
            };
        }
    }

    record ConditionError(Field field, Operator operator, String value) implements ValidationError {}
    record ExistingDrugMedicationError(String atcCode, String formCode, String routeOfAdministrationCode) implements ValidationError {}
    record AndError(ValidationError e1, ValidationError e2) implements ValidationError {}
    record OrError(ValidationError e1, ValidationError e2) implements ValidationError {}
}