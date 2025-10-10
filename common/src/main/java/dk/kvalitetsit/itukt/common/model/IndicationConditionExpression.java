package dk.kvalitetsit.itukt.common.model;

public record IndicationConditionExpression(String requiredValue) implements Expression.Condition {
    public static Field field() {
        return Field.INDICATION;
    }

    @Override
    public boolean validates(ValidationInput validationInput) {
        var fieldValue = validationInput.getByField(IndicationConditionExpression.field());
        return requiredValue.equals(fieldValue);
    }
}
