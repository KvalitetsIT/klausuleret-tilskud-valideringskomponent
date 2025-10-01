package dk.kvalitetsit.itukt.validation.repository.entity;

public record SkippedValidationEntity(
        Long clauseId,
        String actorId,
        String personId) {
}
