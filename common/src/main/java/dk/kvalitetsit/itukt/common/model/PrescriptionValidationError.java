package dk.kvalitetsit.itukt.common.model;

public record PrescriptionValidationError(String drugName, ValidationError content) {
}
