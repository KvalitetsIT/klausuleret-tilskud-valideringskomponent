package dk.kvalitetsit.klaus.repository.model;

import java.util.UUID;

public record ClauseEntity(
        Long id,
        UUID uuid,
        String name,
        Integer version,
        ExpressionEntity expression) {}
