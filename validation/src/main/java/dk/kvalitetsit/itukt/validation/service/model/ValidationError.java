package dk.kvalitetsit.itukt.validation.service.model;

public record ValidationError(
        String clauseCode,
        String clauseText,
        String errorMessage,
        Integer errorCode
        ) implements ValidationResult {
}
