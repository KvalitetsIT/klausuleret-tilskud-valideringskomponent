package dk.kvalitetsit.itukt.common.model;


import java.util.Optional;
import java.util.UUID;

public record Clause(
        String name,
        Optional<UUID> uuid,
        Expression expression
) {
    public Clause(String name, UUID uuid, Expression expression) {
        this(name, Optional.of(uuid), expression);
    }

    public Clause(String name, Expression expression) {
        this(name, Optional.empty(), expression);
    }
}
