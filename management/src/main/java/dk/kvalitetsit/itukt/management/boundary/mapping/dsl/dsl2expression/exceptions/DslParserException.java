package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions;

public abstract sealed class DslParserException extends RuntimeException permits
        UnexpectedValueException,
        UnexpectedAgeValueException,
        IncompleteDslException,
        UnexpectedEmptyMultiValueConditionException,
        UnexpectedExistingDrugMedicationKeysException {

    protected DslParserException(String message) {
        super("Invalid DSL. " + message);
    }
}
