package dk.kvalitetsit.itukt.common.model;

public record IndicationConditionExpression(String requiredValue) implements Expression.Condition {
    public boolean validates(ValidationInput validationInput) {
        return requiredValue.equals(validationInput.indicationCode());
    }
}