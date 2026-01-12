package dk.kvalitetsit.itukt.validation.repository.entity;

/**
 * @param clauseId the primary key associated with the clause
 * @param actorId the ID associated with either the creator or the reporter
 * @param personId the ID associated with citizen for whom the prescription is intended
 */
public record SkippedValidationEntity(
        Long clauseId,
        String actorId,
        String personId) {
}
