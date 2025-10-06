package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

public record NumberConditionExpression(Field field, Operator operator, int value) implements Expression.Condition {
    @Override
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        var fieldValue = validationInput.getByField(field());
        var success = fieldValue instanceof Integer intValue &&
                switch (operator) {
                    case EQUAL -> intValue == this.value;
                    case GREATER_THAN_OR_EQUAL_TO -> intValue >= this.value;
                    case LESS_THAN_OR_EQUAL_TO -> intValue <= this.value;
                    case GREATER_THAN -> intValue > this.value;
                    case LESS_THAN -> intValue < this.value;
                };
        return success
                ? Optional.empty()
                : ValidationError.toField(field).map(f -> new SpecificError(f, operator, String.valueOf(value)));
    }
}