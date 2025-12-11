package dk.kvalitetsit.itukt.common.model;

import java.util.Date;
import java.util.UUID;

public record Clause(
        Long id,
        String name,
        UUID uuid,
        Error error,
        Expression expression,
        Date createdAt
) {
    public record Error(String message, int code) {
    }

    public enum Status {
        DRAFT,
        ACTIVE
    }
}