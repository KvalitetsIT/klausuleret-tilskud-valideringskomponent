package dk.kvalitetsit.itukt.management.boundary;

import java.util.List;

public class ErrorMessages {
    private static final String INVALID_DSL = "Ugyldig klausulbetingelse. ";

    public static String unexpectedValue(String value) {
        return INVALID_DSL + "Uventet værdi: " + value;
    }

    public static String unexpectedEndOfDsl() {
        return "Ufærdig klausulbetingelse";
    }

    public static String unexpectedEmptyMultiValueCondition() {
        return INVALID_DSL + "Betingelse med flere værdier må ikke være tom";
    }

    public static String unexpectedAgeValue(String value) {
        return INVALID_DSL + "Uventet værdi for alder: " + value;
    }

    public static String unexpectedExistingDrugMedicationKeys(List<String> validKeys) {
        return INVALID_DSL + "Eksisterende lægemiddel kan kun have værdier for: " + String.join(", ", validKeys);
    }
}
