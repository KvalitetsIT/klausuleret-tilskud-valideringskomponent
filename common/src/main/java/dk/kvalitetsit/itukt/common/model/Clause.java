package dk.kvalitetsit.itukt.common.model;

import java.util.UUID;

public record Clause(
        Long id,
        String name,
        UUID uuid,
        Error error,
        Expression expression
) {
    public record Error(String message, Integer code){

    }
}
