package dk.kvalitetsit.itukt.common.model;

import java.util.UUID;

public record Clause(
        Long id,
        String name,
        UUID uuid,
        Error error,
        Expression expression
) {
    public record Error(String message, Integer code) {
        public Error {
            if (code > 10999) throw new IllegalStateException(String.format(
                    "The error code (%s) must be less than 11000",
                    code
            ));
            if (code < 10800) throw new IllegalStateException(String.format(
                    "The error code (%s) must be greater than 10800",
                    code
            ));
        }
    }
}
