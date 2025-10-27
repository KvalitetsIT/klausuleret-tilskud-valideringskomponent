package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

public record IndicationConditionExpression(String requiredValue) implements Expression.Condition {
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        return requiredValue.equals(validationInput.indicationCode())
                ? Optional.empty()
                : Optional.of(new ValidationError("indikation skal være " + requiredValue));
    }
}