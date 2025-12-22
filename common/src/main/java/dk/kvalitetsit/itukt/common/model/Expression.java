package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

/**
 * Represents a logical expression that can be validated against a given input.
 * <p>
 * Implementations of this interface define specific types of expressions,
 * such as simple conditions or composite binary expressions.
 */
public sealed interface Expression permits Expression.Condition, BinaryExpression {

    /**
     * Validates this expression against the provided {@link ValidationInput}.
     *
     * @param validationInput the input data used to evaluate and validate the expression
     * @return an {@link Optional} containing a {@link ValidationFailed} instance
     * if the validation fails, or an empty {@code Optional} if the expression is valid
     */
    Optional<ValidationFailed> validates(ValidationInput validationInput);

    /**
     * A sealed subinterface representing expression conditions.
     * <p>
     * Implementations represent specific condition types, such as indication checks,
     * age matching, or existing drug interactions.
     */
    sealed interface Condition
            extends Expression
            permits
            IndicationConditionExpression,
            AgeConditionExpression,
            ExistingDrugMedicationConditionExpression,
            DoctorSpecialityConditionExpression,
            DepartmentConditionExpression {}
}