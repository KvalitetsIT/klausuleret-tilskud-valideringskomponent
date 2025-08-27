package dk.kvalitetsit.itukt.validation.service.model;

public sealed interface ValidationResult permits ValidationError, ValidationSuccess {
}
