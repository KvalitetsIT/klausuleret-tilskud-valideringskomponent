package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static java.lang.String.join;

public sealed interface Expression permits Expression.Condition, BinaryExpression {
    Optional<ValidationError> validates(ValidationInput validationInput);

    sealed interface Condition extends Expression permits IndicationConditionExpression, AgeConditionExpression, ExistingDrugMedicationConditionExpression {
    }

    sealed interface ValidationError permits AndError, UnspecifiedError, OrError, ConditionError {
        default String errorMessage() {
            return switch (this) {
                case AndError ignored -> toErrString(this);
                case ConditionError ignored -> toErrString(this);
                case UnspecifiedError ignored -> toErrString(this);
                case OrError orError -> toErrString(orError.e1) + " eller " + toErrString(orError.e2);
            };
        }

        enum Field { AGE, INDICATION }

        static String toErrString(ValidationError e) {
            return switch (e) {
                case AndError andError -> toErrString(andError.e1) + " og " + toErrString(andError.e2);
                case OrError orError -> "(" + toErrString(orError.e1) + " eller " + toErrString(orError.e2) + ")";
                case ConditionError conditionError -> toErrString(conditionError);
                case UnspecifiedError unspecifiedError -> "<uspecificeret fejl>";
            };
        }

        static String toErrString(ConditionError error) {
            return join(" ",
                    toErrString(error.field()),
                    toErrString(error.operator()),
                    error.value());
        }

        static String toErrString(Field field) {
            return switch (field) {
                case AGE -> "alder";
                case INDICATION -> "indikation";
            };
        }

        static String toErrString(Operator field) {
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
    record UnspecifiedError() implements ValidationError {}
    record AndError(ValidationError e1, ValidationError e2) implements ValidationError {}
    record OrError(ValidationError e1, ValidationError e2) implements ValidationError {}
}