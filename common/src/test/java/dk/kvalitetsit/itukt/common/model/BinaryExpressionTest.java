package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;
import static org.junit.jupiter.api.Assertions.*;

class BinaryExpressionTest {

    private final ConditionError expectedAgeError = new ConditionError(Field.AGE, Operator.EQUAL, "20");
    private final Optional<ValidationFailed> expectedAgeOpt = Optional.of(expectedAgeError);
    private final ConditionError expectedIndicationError = new ConditionError(Field.INDICATION, Operator.EQUAL, "indication");
    private final Optional<ValidationFailed> expectedIndicationOpt = Optional.of(expectedIndicationError);

    @Test
    void validates_WithOrOperatorWhenNeitherLeftOrRightValidates_ReturnsFalse() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(expectedAgeOpt);
        Mockito.when(right.validates(validationInput)).thenReturn(expectedIndicationOpt);

        var validationError = binaryExpression.validates(validationInput);

        assertTrue(validationError.isPresent());
        var binaryError = assertInstanceOf(OrError.class, validationError.get());
        assertEquals(expectedAgeError, binaryError.e1());
        assertEquals(expectedIndicationError, binaryError.e2());
    }

    @Test
    void validates_WithOrOperatorWhenLeftValidatesButNotRight_ReturnsTrue() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.empty());
        Mockito.when(right.validates(validationInput)).thenReturn(expectedAgeOpt);

        boolean success = binaryExpression.validates(validationInput).isEmpty();

        assertTrue(success);
    }

    @Test
    void validates_WithOrOperatorWhenRightValidatesButNotLeft_ReturnsTrue() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(expectedAgeOpt);
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.empty());

        boolean success = binaryExpression.validates(validationInput).isEmpty();

        assertTrue(success);
    }

    @Test
    void validates_WithOrOperatorWhenBothLeftAndRightValidates_ReturnsTrue() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.empty());
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.empty());

        boolean success = binaryExpression.validates(validationInput).isEmpty();

        assertTrue(success);
    }

    @Test
    void validates_WithAndOperatorWhenNeitherLeftOrRightValidates_ReturnsFalse() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(expectedAgeOpt);
        Mockito.when(right.validates(validationInput)).thenReturn(expectedIndicationOpt);

        var validationError = binaryExpression.validates(validationInput);

        assertTrue(validationError.isPresent());
        var binaryError = assertInstanceOf(AndError.class, validationError.get());
        assertEquals(expectedAgeError, binaryError.e1());
        assertEquals(expectedIndicationError, binaryError.e2());
    }

    @Test
    void validates_WithAndOperatorWhenLeftValidatesButNotRight_ReturnsFalse() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.empty());
        Mockito.when(right.validates(validationInput)).thenReturn(expectedAgeOpt);

        var validationError = binaryExpression.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ConditionError.class, validationError.get());
        assertEquals(expectedAgeError, error);
    }

    @Test
    void validates_WithAndOperatorWhenLeftDoesNotValidateButRightValidates_ReturnsFalse() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(expectedAgeOpt);
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.empty());

        var validationError = binaryExpression.validates(validationInput);

        assertTrue(validationError.isPresent());
        var error = assertInstanceOf(ConditionError.class, validationError.get());
        assertEquals(expectedAgeError, error);
    }

    @Test
    void validates_WithAndOperatorWhenBothLeftAndRightValidates_ReturnsTrue() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.empty());
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.empty());

        boolean success = binaryExpression.validates(validationInput).isEmpty();

        assertTrue(success);
    }

    @Test
    void validates_WithAndOperatorWhenBothLeftAndRightRequiresExistingMedication_ReturnsExistingMedicationRequired() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.of(new ExistingDrugMedicationRequired()));
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.of(new ExistingDrugMedicationRequired()));

        var result = binaryExpression.validates(validationInput);

        assertEquals(Optional.of(new ExistingDrugMedicationRequired()), result);
    }

    @Test
    void validates_WithAndOperatorWhenLeftRequiresExistingMedication_ReturnsExistingMedicationRequired() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.of(new ExistingDrugMedicationRequired()));
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.empty());

        var result = binaryExpression.validates(validationInput);

        assertEquals(Optional.of(new ExistingDrugMedicationRequired()), result);
    }

    @Test
    void validates_WithAndOperatorWhenRightRequiresExistingMedication_ReturnsExistingMedicationRequired() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.empty());
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.of(new ExistingDrugMedicationRequired()));

        var result = binaryExpression.validates(validationInput);

        assertEquals(Optional.of(new ExistingDrugMedicationRequired()), result);
    }

    @Test
    void validates_WithOrOperatorWhenBothLeftAndRightRequiresExistingMedication_ReturnsExistingMedicationRequired() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.of(new ExistingDrugMedicationRequired()));
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.of(new ExistingDrugMedicationRequired()));

        var result = binaryExpression.validates(validationInput);

        assertEquals(Optional.of(new ExistingDrugMedicationRequired()), result);
    }

    @Test
    void validates_WithOrOperatorWhenLeftRequiresExistingMedication_ReturnsSuccess() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.of(new ExistingDrugMedicationRequired()));
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.empty());

        var result = binaryExpression.validates(validationInput);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void validates_WithOrOperatorWhenRightRequiresExistingMedication_ReturnsSuccess() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.empty());
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.of(new ExistingDrugMedicationRequired()));

        var result = binaryExpression.validates(validationInput);

        assertEquals(Optional.empty(), result);
    }

    @Test
    @Tag("PerformanceTest")
    void validates_WithOrOperatorWhenLeftValidates_RightShouldNotBeEvaluated() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = new AgeConditionExpression(Operator.EQUAL, 0);
        var rightSpy = Mockito.spy(right);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, rightSpy);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.empty());

        binaryExpression.validates(validationInput);

        Mockito.verify(rightSpy, Mockito.times(0)).validates(validationInput);
    }
}