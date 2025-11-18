package dk.kvalitetsit.itukt.validation.service.model;

/**
 * @param clause The clause that failed.
 * @param message Generated error message from the failed validation
 * @param code Reserved error code for FMK, e.g. 10800
 */
public record ValidationError(Clause clause, String message, Integer code) {
    /**
     * @param code The clause code. e.g. "CHOL"
     * @param text Text from stamdatabasen, e.g. "Kronisk Rhinitis"
     * @param message Error message entered during clause creation
     */
    public record Clause(String code, String text, String message) {}
}