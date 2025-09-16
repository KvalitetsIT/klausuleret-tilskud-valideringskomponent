package dk.kvalitetsit.itukt.common.model;

public record StringConditionExpression(Field field, String requiredValue) implements Expression.Condition {
    @Override
    public boolean validates(ValidationInput validationInput) {
        var fieldValue = validationInput.getByField(field());
        return requiredValue.equals(fieldValue);
    }
}
