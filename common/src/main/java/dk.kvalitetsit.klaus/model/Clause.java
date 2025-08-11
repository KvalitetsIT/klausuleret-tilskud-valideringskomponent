package dk.kvalitetsit.klaus.model;


import java.util.Optional;
import java.util.UUID;

public record Clause(
        String name,
        Optional<UUID> uuid,
        Expression expression
) { }
