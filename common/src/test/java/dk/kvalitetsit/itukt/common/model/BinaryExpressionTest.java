package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinaryExpressionTest {

    @Test
    void validates_WithOrOperatorWhenNeitherLeftOrRightValidates_ReturnsFalse() {
        var left = Mockito.mock(Expression.NotPersisted.Condition.class);
        var right = Mockito.mock(Expression.NotPersisted.Condition.class);

        var binaryExpression = new Expression.NotPersisted.Binary(left, BinaryOperator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(false);
        Mockito.when(right.validates(validationInput)).thenReturn(false);

        boolean success = binaryExpression.validates(validationInput);

        assertFalse(success);
    }

    @Test
    void validates_WithOrOperatorWhenLeftValidatesButNotRight_ReturnsTrue() {
        var left = Mockito.mock(Expression.NotPersisted.Condition.class);
        var right = Mockito.mock(Expression.NotPersisted.Condition.class);
        var binaryExpression = new Expression.NotPersisted.Binary(left, BinaryOperator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(true);
        Mockito.when(right.validates(validationInput)).thenReturn(false);

        boolean success = binaryExpression.validates(validationInput);

        assertTrue(success);
    }

    @Test
    void validates_WithOrOperatorWhenRightValidatesButNotLeft_ReturnsTrue() {
        var left = Mockito.mock(Expression.NotPersisted.Condition.class);
        var right = Mockito.mock(Expression.NotPersisted.Condition.class);
        var binaryExpression = new Expression.NotPersisted.Binary(left, BinaryOperator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(false);
        Mockito.when(right.validates(validationInput)).thenReturn(true);

        boolean success = binaryExpression.validates(validationInput);

        assertTrue(success);
    }

    @Test
    void validates_WithOrOperatorWhenBothLeftAndRightValidates_ReturnsTrue() {
        var left = Mockito.mock(Expression.NotPersisted.Condition.class);
        var right = Mockito.mock(Expression.NotPersisted.Condition.class);
        var binaryExpression = new Expression.NotPersisted.Binary(left, BinaryOperator.OR, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(true);
        Mockito.when(right.validates(validationInput)).thenReturn(true);

        boolean success = binaryExpression.validates(validationInput);

        assertTrue(success);
    }

    @Test
    void validates_WithAndOperatorWhenNeitherLeftOrRightValidates_ReturnsFalse() {
        var left = Mockito.mock(Expression.NotPersisted.Condition.class);
        var right = Mockito.mock(Expression.NotPersisted.Condition.class);
        var binaryExpression = new Expression.NotPersisted.Binary(left, BinaryOperator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(false);
        Mockito.when(right.validates(validationInput)).thenReturn(false);

        boolean success = binaryExpression.validates(validationInput);

        assertFalse(success);
    }

    @Test
    void validates_WithAndOperatorWhenLeftValidatesButNotRight_ReturnsFalse() {
        var left = Mockito.mock(Expression.NotPersisted.Condition.class);
        var right = Mockito.mock(Expression.NotPersisted.Condition.class);
        var binaryExpression = new Expression.NotPersisted.Binary(left, BinaryOperator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(true);
        Mockito.when(right.validates(validationInput)).thenReturn(false);

        boolean success = binaryExpression.validates(validationInput);

        assertFalse(success);
    }

    @Test
    void validates_WithAndOperatorWhenLeftDoesNotValidateButRightValidates_ReturnsFalse() {
        var left = Mockito.mock(Expression.NotPersisted.Condition.class);
        var right = Mockito.mock(Expression.NotPersisted.Condition.class);
        var binaryExpression = new Expression.NotPersisted.Binary(left, BinaryOperator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(false);
        Mockito.when(right.validates(validationInput)).thenReturn(true);

        boolean success = binaryExpression.validates(validationInput);

        assertFalse(success);
    }

    @Test
    void validates_WithAndOperatorWhenBothLeftAndRightValidates_ReturnsTrue() {
        var left = Mockito.mock(Expression.NotPersisted.Condition.class);
        var right = Mockito.mock(Expression.NotPersisted.Condition.class);
        var binaryExpression = new Expression.NotPersisted.Binary(left, BinaryOperator.AND, right);
        var validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(left.validates(validationInput)).thenReturn(true);
        Mockito.when(right.validates(validationInput)).thenReturn(true);

        boolean success = binaryExpression.validates(validationInput);

        assertTrue(success);
    }
}