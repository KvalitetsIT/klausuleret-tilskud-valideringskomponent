package dk.kvalitetsit.itukt.management.exceptions;

import java.util.Set;

public final class UnexpectedExistingDrugMedicationKeysException extends DslParserException {
    private final Set<String> validKeys;

    public UnexpectedExistingDrugMedicationKeysException(Set<String> validKeys) {
        super("Existing drug medication condition keys must be one of: " + validKeys);
        this.validKeys = validKeys;
    }

    public Set<String> getValidKeys() {
        return validKeys;
    }
}
