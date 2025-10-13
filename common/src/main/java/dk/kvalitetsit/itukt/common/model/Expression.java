package dk.kvalitetsit.itukt.common.model;


public sealed interface Expression permits Expression.Condition, BinaryExpression {
    boolean validates(ValidationInput validationInput);

    sealed interface Condition extends Expression permits IndicationConditionExpression, AgeConditionExpression, ExistingDrugMedicationConditionExpression {}
}