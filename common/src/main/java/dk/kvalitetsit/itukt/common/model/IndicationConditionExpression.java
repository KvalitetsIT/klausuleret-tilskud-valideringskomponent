package dk.kvalitetsit.itukt.common.model;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;

import java.util.Optional;

public record IndicationConditionExpression(String requiredValue) implements Expression.Condition {
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        return requiredValue.equals(validationInput.indicationCode())
                ? Optional.empty()
                : Optional.of(new ConditionError(ValidationError.Field.INDICATION, Operator.EQUAL, requiredValue));
    }
}