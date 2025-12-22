package dk.kvalitetsit.itukt.common.model;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a clause that is evaluated in a validation context.
 *
 * @param id         the numeric identifier of the clause which is given by the database
 * @param name       the human-readable name of the clause
 * @param uuid       An identifier for the clause which is uniquely assigned to every new version and hides the {@param id}
 * @param error      an error associated with the clause, containing a message and code
 * @param expression the logical expression that defines the condition(s) of the clause
 */
public record Clause(
        Long id,
        String name,
        UUID uuid,
        Error error,
        Expression expression,
        Optional<Date> validFrom
) {

    /**
     * Indicates whether the clause is either a draft or active
     * Note: If draft, the clause won't be evaluated
     */
    public enum Status {
        DRAFT,
        ACTIVE
    }

    /**
     * Represents an error associated with a {@link Clause}.
     *
     * @param message a descriptive error message
     * @param code    a numeric error code representing the type of error (10800-11000)
     */
    public record Error(String message, int code) {
    }
}