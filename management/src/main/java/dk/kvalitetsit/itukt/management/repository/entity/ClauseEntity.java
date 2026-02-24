package dk.kvalitetsit.itukt.management.repository.entity;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public record ClauseEntity(
        Long id,
        UUID uuid,
        String name,
        Clause.Status status,
        int errorCode,
        String errorMessage,
        ExpressionEntity expression,
        Optional<Date> validFrom
) {
}
