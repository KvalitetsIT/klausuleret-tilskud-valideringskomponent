package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;

public record AgeConditionExpression(Operator operator, int value) implements Expression.Condition {
    public Optional<ValidationError> validates(ValidationInput validationInput) {
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