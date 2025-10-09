package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.join;

public sealed interface Expression permits Expression.Condition, BinaryExpression {
    Optional<ValidationError> validates(ValidationInput validationInput);

    sealed interface Condition extends Expression permits StringConditionExpression, NumberConditionExpression, ExistingDrugMedicationConditionExpression {
        enum Field {AGE, INDICATION, EXISTING_DRUG_MEDICATION}
    }

    sealed interface ValidationError permits AndError, HistoryError, UnsupportedError, OrError, SpecificError {
        default String errorMessage() {
            return toErrorString(this);
        }

        enum Field {AGE, INDICATION}

        static String toErrorString(ValidationError e) {
            return switch (e) {
                case AndError andError -> toErrorString(andError);
                case OrError(var e1, var e2) -> toErrorString(e1) + " eller " + toErrorString(e2);
                case SpecificError specificError -> toErrorString(specificError);
                case HistoryError historyError -> toErrorString(historyError);
                case UnsupportedError ignored -> "<en valideringsfejl opstod, som ikke har en tilhørende fejlbesked>";
            };
        }

        static String toErrorString(AndError error) {
            Function<ValidationError, String> parenthesesIfNecessary = e -> e instanceof OrError ? "(" + toErrorString(e) + ")" : toErrorString(e);
            return parenthesesIfNecessary.apply(error.e1) + " og " + parenthesesIfNecessary.apply(error.e2);
        }

        static String toErrorString(HistoryError historyError) {
            return "Tidligere medicinsk behandling med følgende påkrævet:" +
                    " ATC = " + historyError.atcCode +
                    ", Formkode = " + historyError.formCode +
                    ", Administrationsrutekode = " + historyError.routeOfAdministrationCode;
        }

        static String toErrorString(SpecificError error) {
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

    record SpecificError(Field field, Operator operator, String value) implements ValidationError {}
    record HistoryError(String atcCode, String formCode, String routeOfAdministrationCode) implements ValidationError {}
    record AndError(ValidationError e1, ValidationError e2) implements ValidationError {}
    record OrError(ValidationError e1, ValidationError e2) implements ValidationError {}
    record UnsupportedError() implements ValidationError {}
}