package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationError.*;
import static org.junit.jupiter.api.Assertions.*;

class BinaryExpressionTest {

    private final String expectedAge = "20", expectedIndication = "indication";;
    private final ConditionError expectedAgeError = new ConditionError(ValidationError.Field.AGE, Operator.EQUAL, expectedAge);
    private final Optional<ValidationError> expectedAgeOpt = Optional.of(expectedAgeError);
    private final ConditionError expectedIndicationError = new ConditionError(ValidationError.Field.INDICATION, Operator.EQUAL, expectedIndication);
    private final Optional<ValidationError> expectedIndicationOpt = Optional.of(expectedIndicationError);

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
        var binaryError = assertInstanceOf(ValidationError.OrError.class, validationError.get());
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
        var binaryError = assertInstanceOf(ValidationError.AndError.class, validationError.get());
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
        var error = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
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
        var error = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
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
}