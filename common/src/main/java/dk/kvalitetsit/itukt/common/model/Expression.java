package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

public sealed interface Expression permits Expression.Condition, BinaryExpression {
    Optional<ValidationFailed> validates(ValidationInput validationInput);

    sealed interface Condition extends Expression permits IndicationConditionExpression, AgeConditionExpression, ExistingDrugMedicationConditionExpression {
    }
}