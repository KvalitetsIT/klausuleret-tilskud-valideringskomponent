package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions;

import java.util.List;

public final class UnexpectedExistingDrugMedicationKeysException extends DslParserException {
    private final List<String> validKeys;

    public UnexpectedExistingDrugMedicationKeysException(List<String> validKeys) {
        super("Existing drug medication condition keys must be one of: " + validKeys);
        this.validKeys = validKeys;
    }

    public List<String> getValidKeys() {
        return validKeys;
    }
}
