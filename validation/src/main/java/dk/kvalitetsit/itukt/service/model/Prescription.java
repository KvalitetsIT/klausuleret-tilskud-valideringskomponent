package dk.kvalitetsit.itukt.service.model;

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
