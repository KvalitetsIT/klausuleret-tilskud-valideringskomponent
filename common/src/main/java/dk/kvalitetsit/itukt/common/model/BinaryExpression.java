package dk.kvalitetsit.itukt.common.model;

public record BinaryExpression(Expression left, Operator operator, Expression right) implements Expression {

    @Override
    public boolean validates(ValidationInput validationInput) {
        return switch (operator) {
            case AND -> left.validates(validationInput) && right.validates(validationInput);
            case OR -> left.validates(validationInput) || right.validates(validationInput);
        };
    }

    public enum Operator {OR, AND}
}
