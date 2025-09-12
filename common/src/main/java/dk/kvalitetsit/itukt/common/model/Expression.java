package dk.kvalitetsit.itukt.common.model;


public sealed interface Expression permits Expression.Condition, BinaryExpression {
    boolean validates(ValidationInput validationInput);

    sealed interface Condition extends Expression permits StringConditionExpression, NumberConditionExpression {
        enum Field {AGE, INDICATION}

        boolean matches(Object value);
        Field field();

        @Override
        default boolean validates(ValidationInput validationInput) {
            return matches(validationInput.getByField(field()));
        }
    }
}