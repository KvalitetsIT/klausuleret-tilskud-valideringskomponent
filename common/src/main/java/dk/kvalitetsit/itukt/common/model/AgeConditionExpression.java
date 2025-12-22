package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;

/**
 * An expression representing a condition on a citizen’s age.
 * This condition checks whether the age from a given {@link ValidationInput}
 * satisfies the comparison defined by the provided {@link Operator} and target value.
 * @param operator the comparison operator; e.g. EQUAL, GREATER_THAN, LESS_THAN_OR_EQUAL_TO, etc.
 * @param value the age to compare against; e.g. 18
 */
public record AgeConditionExpression(Operator operator, int value) implements Expression.Condition {

    /**
     * Validates the age condition against the provided {@link ValidationInput}.
     * The input’s age is retrieved and compared against {@code value} using
     * the specified {@code operator}. If the comparison succeeds, the method
     * returns an empty {@code Optional}. Otherwise, it returns an {@code Optional}
     * containing a {@link ConditionError} that identifies the age field, operator,
     * and expected value.
     *
     * @param validationInput the input containing the citizen’s age to validate
     * @return an empty {@code Optional} if the age condition is satisfied; otherwise an {@code Optional} containing the validation failure
     */
    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        var intValue = validationInput.citizenAge();
        var success = switch (operator) {
                    case EQUAL -> intValue == this.value;
                    case GREATER_THAN_OR_EQUAL_TO -> intValue >= this.value;
                    case LESS_THAN_OR_EQUAL_TO -> intValue <= this.value;
                    case GREATER_THAN -> intValue > this.value;
                    case LESS_THAN -> intValue < this.value;
                };
        return success
                ? Optional.empty()
                : Optional.of(new ConditionError(ValidationError.Field.AGE, operator, String.valueOf(value)));
    }
}