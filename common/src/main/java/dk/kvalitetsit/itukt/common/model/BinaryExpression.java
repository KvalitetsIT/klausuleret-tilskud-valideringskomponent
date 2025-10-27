package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.function.Supplier;

public record BinaryExpression(Expression left, Operator operator, Expression right) implements Expression {

    @Override
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        var leftError = left.validates(validationInput);
        Supplier<Optional<ValidationError>> rightErrorLazy = () -> right.validates(validationInput); // Only validate right when necessary
        return switch (operator) {
            case AND -> andError(leftError, rightErrorLazy.get());
            case OR -> leftError.flatMap(l -> rightErrorLazy.get().map(r -> new ValidationError(l.error() + " eller " + r.error(), true)));
        };
    }

    private String parenthesesIfNecessary(ValidationError e) {
        return e.isOrError() ? "(" + e.error() + ")" : e.error();
    }

    private Optional<ValidationError> andError(Optional<ValidationError> leftError, Optional<ValidationError> rightError) {
        Optional<ValidationError> bothError  = leftError.flatMap(l -> rightError.map(r -> new ValidationError(parenthesesIfNecessary(l) + " og " + parenthesesIfNecessary(r))));
        return bothError.or(() -> leftError.or(() -> rightError));
    }

    public enum Operator {OR, AND}
}