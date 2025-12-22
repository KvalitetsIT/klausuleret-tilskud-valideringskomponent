package dk.kvalitetsit.itukt.common.model;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;

import java.util.Optional;

/**
 * An expression representing a condition that checks whether a given
 * {@link ValidationInput}'s indication code matches a required value.
 * <p>
 * This condition is satisfied if the input's indication code is equal
 * to the {@code requiredValue}. Otherwise, a {@link ValidationFailed}
 * instance is returned describing the mismatch.
 *
 * @param requiredValue the indication code that must match the input for validation to succeed
 */
public record IndicationConditionExpression(String requiredValue) implements Expression.Condition {

    /**
     * Validates the indication condition against the provided {@link ValidationInput}.
     * <p>
     * The condition is considered valid when the input's indication code
     * equals the {@code requiredValue}. If the values differ, a
     * {@link ConditionError} is returned identifying the failure.
     *
     * @param validationInput the input containing the indication code to validate
     * @return an empty {@code Optional} if validation succeeds; otherwise an {@code Optional} containing the validation failure
     */
    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        return requiredValue.equals(validationInput.indicationCode())
                ? Optional.empty()
                : Optional.of(new ConditionError(ValidationError.Field.INDICATION, Operator.EQUAL, requiredValue));
    }
}