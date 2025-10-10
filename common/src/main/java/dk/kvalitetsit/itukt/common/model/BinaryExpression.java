package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.function.Supplier;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;

public record BinaryExpression(Expression left, Operator operator, Expression right) implements Expression {
    public enum Operator {OR, AND}

    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        var leftError = left.validates(validationInput);
        Supplier<Optional<ValidationFailed>> rightErrorLazy = () -> right.validates(validationInput); // Only validate right when necessary
        return switch (operator) {
            case AND -> andError(leftError, rightErrorLazy);
            case OR -> leftError.flatMap(l -> rightErrorLazy.get().map(r -> combineOr(l, r)));
        };
    }

    static Optional<ValidationFailed> andError(Optional<ValidationFailed> optLeftFailure, Supplier<Optional<ValidationFailed>> rightLazy) {
        Optional<ValidationFailed> bothErrorOrExistingMedicationMissing = optLeftFailure.flatMap(leftFailure -> switch (leftFailure) {
            case ExistingDrugMedicationRequired ex -> Optional.of(ex);
            case ValidationError leftError -> rightLazy.get().map(r -> switch (r) {
                case ExistingDrugMedicationRequired ex -> ex;
                case ValidationError rightError -> new AndError(leftError, rightError);
            });
        });
        return bothErrorOrExistingMedicationMissing.or(() -> optLeftFailure.or(rightLazy));
    }

    record Pair(ValidationFailed vf1, ValidationFailed vf2) {} // To help pattern matching

    static ValidationFailed combineOr(ValidationFailed left, ValidationFailed right) {
        return switch (new Pair(left, right)) {
            case Pair(ExistingDrugMedicationRequired l, ValidationError __) -> l;
            case Pair(ValidationError __, ExistingDrugMedicationRequired r) -> r;
            case Pair(ExistingDrugMedicationRequired l, ExistingDrugMedicationRequired __) -> l;
            case Pair(ValidationError l, ValidationError r) -> new OrError(l, r);
        };
    }
}