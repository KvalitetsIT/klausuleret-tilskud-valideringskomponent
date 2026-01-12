package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.function.Supplier;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;


/**
 * Represents a binary logical expression combining two {@link Expression} instances with a logical operator (AND / OR).
 * @param left the left-hand side expression
 * @param operator the logical operator combining the two expressions
 * @param right the right-hand side expression
 */
public record BinaryExpression(Expression left, Operator operator, Expression right) implements Expression {

    /**
     * Logical operators supported for binary expressions.
     */
    public enum Operator {
        OR,
        AND
    }

    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        var leftError = left.validates(validationInput);
        Supplier<Optional<ValidationFailed>> rightErrorLazy = () -> right.validates(validationInput); // Only validate right when necessary
        return switch (operator) {
            case AND -> andError(leftError, rightErrorLazy);
            case OR -> leftError.flatMap(l -> rightErrorLazy.get().map(r -> combineOr(l, r)));
        };
    }

    /**
     * Combines errors for an AND operation.
     * <p>
     * If both expressions fail, their errors are combined into an {@link AndError}.
     * Special handling is applied for {@link ExistingDrugMedicationRequired} errors.
     *
     * @param optLeftFailure the optional validation failure from the left expression
     * @param rightLazy a lazy supplier for the right expression's validation failure
     * @return an {@link Optional} containing the combined validation failure if present
     */
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

    /**
     * Helper record to pair two validation failures for pattern matching.
     */
    record Pair(ValidationFailed vf1, ValidationFailed vf2) {}

    /**
     * Combines two validation failures for an OR operation.
     * <p>
     * If either failure is an {@link ExistingDrugMedicationRequired}, it takes precedence.
     * Otherwise, two {@link ValidationError} instances are combined into an {@link OrError}.
     *
     * @param left the left validation failure
     * @param right the right validation failure
     * @return a combined {@link ValidationFailed} representing the OR logic
     */
    static ValidationFailed combineOr(ValidationFailed left, ValidationFailed right) {
        return switch (new Pair(left, right)) {
            case Pair(ExistingDrugMedicationRequired l, ValidationError __) -> l;
            case Pair(ValidationError __, ExistingDrugMedicationRequired r) -> r;
            case Pair(ExistingDrugMedicationRequired l, ExistingDrugMedicationRequired __) -> l;
            case Pair(ValidationError l, ValidationError r) -> new OrError(l, r);
        };
    }
}