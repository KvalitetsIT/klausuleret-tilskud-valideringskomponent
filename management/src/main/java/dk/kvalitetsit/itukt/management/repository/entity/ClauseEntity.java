package dk.kvalitetsit.itukt.management.repository.entity;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public record ClauseEntity(
        Long id,
        UUID uuid,
        String name,
        int errorCode,
        String errorMessage,
        ExpressionEntity expression,
        Optional<Date> validFrom
) {
}
