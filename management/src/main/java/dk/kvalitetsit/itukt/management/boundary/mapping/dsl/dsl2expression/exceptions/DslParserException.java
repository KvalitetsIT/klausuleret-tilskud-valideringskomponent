package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions;

import dk.kvalitetsit.itukt.management.ManagementException;

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
