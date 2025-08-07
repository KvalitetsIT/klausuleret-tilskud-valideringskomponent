package dk.kvalitetsit.itukt.repository.model;

import java.util.UUID;

public record ClauseEntity(
        Long id,
        UUID uuid,
        String name,
        ExpressionEntity expression) {}
