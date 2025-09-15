package dk.kvalitetsit.itukt.common.model;


import java.util.UUID;

public record Clause(
        String name,
        UUID uuid,
        Integer errorCode,
        Expression expression
) {
    public Clause(String name, UUID uuid, Expression expression) {
        this(name, Optional.of(uuid), expression);
    }

    public Clause(String name, Expression expression) {
        this(name, Optional.empty(), expression);
    }
}
