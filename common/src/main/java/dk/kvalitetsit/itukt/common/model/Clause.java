package dk.kvalitetsit.itukt.common.model;

import java.util.UUID;

public record Clause(
        Long id,
        String name,
        UUID uuid,
        Integer errorCode,
        Expression expression
) { }
