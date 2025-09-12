package dk.kvalitetsit.itukt.common.model;

public record StringConditionExpression(Field field, String requiredValue) implements Expression.Condition {
    @Override
    public boolean matches(Object value) {
        return requiredValue.equals(value);
    }
}
