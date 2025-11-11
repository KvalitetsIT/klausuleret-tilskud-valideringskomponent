package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.function.Supplier;

import static dk.kvalitetsit.itukt.common.model.BinaryExpression.OptionalValidationError.*;
import static dk.kvalitetsit.itukt.common.model.ValidationError.*;
import static java.util.Optional.*;

public record BinaryExpression(Expression left, Operator operator, Expression right) implements Expression {
    public enum Operator {OR, AND}

    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        var leftError = fromOptional(left.validates(validationInput));
        Supplier<OptionalValidationError> rightError = () -> fromOptional(right.validates(validationInput));
        return switch (operator) {
            case AND -> and(leftError, rightError.get());
            case OR -> or(leftError, rightError);
        };
    }

    static Optional<ValidationFailed> and(OptionalValidationError left, OptionalValidationError right) {
        return switch (new Pair(left, right)) {
            case Pair(Present(ExistingDrugMedicationRequired existingRequired), var __) -> of(existingRequired);
            case Pair(var __, Present(ExistingDrugMedicationRequired existingRequired)) -> of(existingRequired);
            case Pair(Present(ValidationError l), Present(ValidationError r)) -> of(new AndError(l, r));
            case Pair(Present(ValidationError l), var __) -> of(l);
            case Pair(var __, Present(ValidationError r)) -> of(r);
            case Pair(Empty a, var __) -> empty();
            case Pair(var __, Empty b) -> empty();
        };
    }

    static Optional<ValidationFailed> or(OptionalValidationError left, Supplier<OptionalValidationError> right) {
        if (left instanceof Empty)
            return empty();
        else
            return switch (new Pair(left, right.get())) {
                case Pair(Empty l, var __) -> empty();
                case Pair(var __, Empty r) -> empty();
                case Pair(Present(ValidationError l), Present(ValidationError r)) -> of(new OrError(l, r));
                case Pair(Present(ExistingDrugMedicationRequired existingRequired), var __) -> of(existingRequired);
                case Pair(var __, Present(ExistingDrugMedicationRequired existingRequired)) -> of(existingRequired);
                case Pair(Present(ValidationError l), var __) -> empty(); // Only needed to satisfy the compiler, this case will never be hit
                case Pair(var __, Present(ValidationError r)) -> empty(); // Only needed to satisfy the compiler, this case will never be hit
            };
    }

    // Java cannot pattern match on Optional. This wrapper class allows it.
    sealed interface OptionalValidationError permits Present, Empty {
        record Present(ValidationFailed f) implements OptionalValidationError {}
        record Empty() implements OptionalValidationError {}

        static OptionalValidationError fromOptional(Optional<ValidationFailed> o) {
            return o.<OptionalValidationError>map(Present::new).orElse(new Empty());
        }
    }

    // To help pattern matching
    record Pair(OptionalValidationError vf1, OptionalValidationError vf2) {}
}