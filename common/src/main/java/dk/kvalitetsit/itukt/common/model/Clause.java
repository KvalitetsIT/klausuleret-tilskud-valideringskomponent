package dk.kvalitetsit.itukt.common.model;


import java.util.UUID;

public record Clause(
        String name,
        UUID uuid,
        Expression expression
) { }
