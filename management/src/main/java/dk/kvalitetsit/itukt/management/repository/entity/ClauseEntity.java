package dk.kvalitetsit.itukt.management.repository.entity;

import java.util.UUID;

public record ClauseEntity(
        Long id,
        UUID uuid,
        String name,
        ExpressionEntity expression) {}
