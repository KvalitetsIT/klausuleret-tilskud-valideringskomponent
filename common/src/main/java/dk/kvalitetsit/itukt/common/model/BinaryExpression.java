package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

public record BinaryExpression(Expression left, Operator operator, Expression right) implements Expression {

    @Override
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        var l = left.validates(validationInput);
        var r = right.validates(validationInput);
        return switch (operator) {
            case AND -> andError(l, r);
            case OR -> orError(l, r);
        };
    }

    private Optional<ValidationError> andError(Optional<ValidationError> left, Optional<ValidationError> right) {
        Optional<ValidationError> bothError  = left.flatMap(l -> right.map(r -> new AndError(l, r)));
        return bothError.or(() -> left.or(() -> right));
    }

    private Optional<ValidationError> orError(Optional<ValidationError> left, Optional<ValidationError> right) {
        return left.flatMap(l -> right.map(r -> new OrError(l, r)));
    }

    public enum Operator {OR, AND}
}