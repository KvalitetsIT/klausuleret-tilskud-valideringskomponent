package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.ConditionError;

public record IndicationConditionExpression(String requiredValue) implements Expression.Condition {
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        return requiredValue.equals(validationInput.indicationCode())
                ? Optional.empty()
                : Optional.of(new ConditionError(ConditionError.Field.INDICATION, Operator.EQUAL, requiredValue));
    }
}