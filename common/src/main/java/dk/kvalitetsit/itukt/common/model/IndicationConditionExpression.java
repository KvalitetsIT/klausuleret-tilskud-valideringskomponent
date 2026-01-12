package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.ConditionError;

/**
 * An expression representing a condition that checks whether a given
 * {@link ValidationInput}'s indication code matches a required value.
 * <p>
 * This condition is satisfied if the input's indication code is equal
 * to the {@code requiredValue}.
 *
 * @param requiredValue the indication code that must match the input for validation to succeed
 */
public record IndicationConditionExpression(String requiredValue) implements Expression.Condition {

    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        return requiredValue.equals(validationInput.indicationCode())
                ? Optional.empty()
                : Optional.of(new ConditionError(ValidationError.Field.INDICATION, Operator.EQUAL, requiredValue));
    }
}