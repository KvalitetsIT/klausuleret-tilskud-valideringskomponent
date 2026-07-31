package dk.kvalitetsit.itukt.management.exceptions;

public abstract sealed class DslParserException extends ManagementException permits
        UnexpectedValueException,
        UnexpectedAgeValueException,
        IncompleteDslException,
        UnexpectedEmptyMultiValueConditionException,
        UnexpectedExistingDrugMedicationKeysException {

    protected DslParserException(String message) {
        super("Invalid DSL. " + message);
    }
}
