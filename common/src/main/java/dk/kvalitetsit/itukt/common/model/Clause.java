package dk.kvalitetsit.itukt.common.model;


import java.util.Optional;
import java.util.UUID;

public record Clause(
        String name,
        Optional<UUID> uuid,
        Expression expression
) { }
