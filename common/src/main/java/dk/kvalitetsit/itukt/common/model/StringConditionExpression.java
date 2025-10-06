package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.Expression.ValidationError.toField;

public record StringConditionExpression(Field field, String requiredValue) implements Expression.Condition {
    @Override
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        var fieldValue = validationInput.getByField(field());
        return requiredValue.equals(fieldValue)
                ? Optional.empty()
                : toField(field).map(f -> new SpecificError(f, Operator.EQUAL, requiredValue));
    }
}