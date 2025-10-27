package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinaryExpressionTest {

    private final Optional<ValidationError> someError = Optional.of(new ValidationError("error")); // Der findes også en binding her, som også skulle ændres.

    @Test
    void validates_WithOrOperatorWhenNeitherLeftOrRightValidates_ReturnsFalse() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(someError);
        Mockito.when(right.validates(validationInput)).thenReturn(someError);

        boolean success = binaryExpression.validates(validationInput).isEmpty();

        assertFalse(success);
    }

    @Test
    void validates_WithOrOperatorWhenLeftValidatesButNotRight_ReturnsTrue() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.empty());
        Mockito.when(right.validates(validationInput)).thenReturn(someError);

        boolean success = binaryExpression.validates(validationInput).isEmpty();

        assertTrue(success);
    }

    @Test
    void validates_WithOrOperatorWhenRightValidatesButNotLeft_ReturnsTrue() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(someError);
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
        Mockito.when(left.validates(validationInput)).thenReturn(someError);
        Mockito.when(right.validates(validationInput)).thenReturn(someError);

        boolean success = binaryExpression.validates(validationInput).isEmpty();

        assertFalse(success);
    }

    @Test
    void validates_WithAndOperatorWhenLeftValidatesButNotRight_ReturnsFalse() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(Optional.empty());
        Mockito.when(right.validates(validationInput)).thenReturn(someError);

        boolean success = binaryExpression.validates(validationInput).isEmpty();

        assertFalse(success);
    }

    @Test
    void validates_WithAndOperatorWhenLeftDoesNotValidateButRightValidates_ReturnsFalse() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(AgeConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(someError);
        Mockito.when(right.validates(validationInput)).thenReturn(Optional.empty());

        boolean success = binaryExpression.validates(validationInput).isEmpty();

        assertFalse(success);
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