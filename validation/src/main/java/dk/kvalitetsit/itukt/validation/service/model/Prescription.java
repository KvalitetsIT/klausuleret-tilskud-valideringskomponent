package dk.kvalitetsit.itukt.validation.service.model;

import org.openapitools.model.Actor;

import java.time.Instant;

public record Prescription(
        Actor createdBy,
        Actor reportedBy,
        String drugIdentifier,
        String indicationCode,
        Instant createdDateTime
) {
}
