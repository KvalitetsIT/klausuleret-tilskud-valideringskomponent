package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.function.Supplier;

import static dk.kvalitetsit.itukt.common.model.BinaryExpression.Option.*;
import static dk.kvalitetsit.itukt.common.model.ValidationError.*;
import static java.util.Optional.*;

public record BinaryExpression(Expression left, Operator operator, Expression right) implements Expression {
    public enum Operator {OR, AND}

    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        var leftError = fromOptional(left.validates(validationInput));
        Supplier<Option<ValidationFailed>> rightError = () -> fromOptional(right.validates(validationInput));
        return switch (operator) {
            case AND -> and(leftError, rightError.get());
            case OR -> or(leftError, rightError);
        };
    }

    static Optional<ValidationFailed> and(Option<ValidationFailed> left, Option<ValidationFailed> right) {
        return switch (new Pair<>(left, right)) {
            case Pair(Present(ExistingDrugMedicationRequired existingRequired), var __) -> of(existingRequired);
            case Pair(var __, Present(ExistingDrugMedicationRequired existingRequired)) -> of(existingRequired);
            case Pair(Present(ValidationError l), Present(ValidationError r)) -> of(new AndError(l, r));
            case Pair(Present(ValidationError l), var __) -> of(l);
            case Pair(var __, Present(ValidationError r)) -> of(r);
            case Pair(Empty a, var __) -> empty();
            case Pair(var __, Empty b) -> empty();
        };
    }

    static Optional<ValidationFailed> or(Option<ValidationFailed> left, Supplier<Option<ValidationFailed>> right) {
        if (left instanceof Empty)
            return empty();
        else
            return switch (new Pair<>(left, right.get())) {
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
    sealed interface Option<A> permits Present, Empty {
        record Present<B>(B value) implements Option<B> {}
        record Empty() implements Option {}

        static <C> Option<C> fromOptional(Optional<C> o) {
            Option<C> empty = new Empty();
            return o.<Option<C>>map(Present::new).orElse(empty);
        }
    }

    // To help pattern matching
    record Pair<A, B>(Option<A> vf1, Option<B> vf2) {}
}