package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.Expression.ValidationFailed.*;

public record NumberConditionExpression(Field field, Operator operator, int value) implements Expression.Condition {
    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
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
                : Optional.of(toError(field, operator, String.valueOf(value)));
    }

    private ValidationError toError(Field field, Operator operator, String value) {
        return switch(field) {
            case AGE -> new SpecificError(ValidationError.Field.AGE, operator, value);
            case INDICATION, EXISTING_DRUG_MEDICATION -> new UnsupportedError();
        };
    }
}