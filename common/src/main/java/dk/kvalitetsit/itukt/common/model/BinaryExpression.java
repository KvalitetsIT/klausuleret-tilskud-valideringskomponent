package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.function.Supplier;

public record BinaryExpression(Expression left, Operator operator, Expression right) implements Expression {

    @Override
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        return switch (operator) {
            case AND -> andError(left, right, validationInput);
            case OR -> orError(left, right, validationInput);
        };
    }

    private Optional<ValidationError> andError(Expression leftEx, Expression rightEx, ValidationInput validationInput) {
        var left = leftEx.validates(validationInput);
        var right = rightEx.validates(validationInput);
        Optional<ValidationError> bothError  = left.flatMap(l -> right.map(r -> new AndError(l, r)));
        return bothError.or(() -> left.or(() -> right));
    }

    private Optional<ValidationError> orError(Expression leftEx, Expression rightEx, ValidationInput validationInput) {
        var left = leftEx.validates(validationInput);
        // only evaluate the right expression if needed
        Supplier<Optional<ValidationError>> rightLazy = () -> rightEx.validates(validationInput);
        return left.flatMap(l -> rightLazy.get().map(r -> new OrError(l, r)));
    }

    public enum Operator {OR, AND}
}