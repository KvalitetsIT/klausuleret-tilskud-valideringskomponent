package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberConditionExpressionTest {
    @Test
    void matches_WhenValueIsNotAnInt_ReturnsFalse() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.EQUAL, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn("Not an int");

        boolean validates = numberCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WithEqualsConditionWhenValueIsBiggerThanRequired_ReturnsFalse() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.EQUAL, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(6);

        boolean validates = numberCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WithEqualsConditionWhenValueIsLessThanRequired_ReturnsFalse() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.EQUAL, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(4);

        boolean validates = numberCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WithEqualsConditionWhenValueIsEqualToRequired_ReturnsTrue() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.EQUAL, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(5);

        boolean validates = numberCondition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithGreaterThanOrEqualToConditionWhenValueIsLessThanRequired_ReturnsFalse() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.GREATER_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(4);

        boolean validates = numberCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WithGreaterThanOrEqualToConditionWhenValueIsEqualToRequired_ReturnsTrue() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.GREATER_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(5);

        boolean validates = numberCondition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithGreaterThanOrEqualToConditionWhenValueIsBiggerThanRequired_ReturnsTrue() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.GREATER_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(6);

        boolean validates = numberCondition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanOrEqualToConditionWhenValueIsLessThanRequired_ReturnsTrue() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.LESS_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(4);

        boolean validates = numberCondition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanOrEqualToConditionWhenValueIsEqualToRequired_ReturnsTrue() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.LESS_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(5);

        boolean validates = numberCondition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanOrEqualToConditionWhenValueIsBiggerThanRequired_ReturnsFalse() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.LESS_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(6);

        boolean validates = numberCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WithGreaterThanConditionWhenValueIsLessThanRequired_ReturnsFalse() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.GREATER_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(4);

        boolean validates = numberCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WithGreaterThanConditionWhenValueIsEqualToRequired_ReturnsFalse() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.GREATER_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(5);

        boolean validates = numberCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WithGreaterThanConditionWhenValueIsBiggerThanRequired_ReturnsTrue() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.GREATER_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(6);

        boolean validates = numberCondition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanConditionWhenValueIsLessThanRequired_ReturnsTrue() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.LESS_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(4);

        boolean validates = numberCondition.validates(validationInput);

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanConditionWhenValueIsEqualToRequired_ReturnsFalse() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.LESS_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(5);

        boolean validates = numberCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WithLessThanConditionWhenValueIsBiggerThanRequired_ReturnsFalse() {
        var numberCondition = new NumberConditionExpression(Expression.Condition.Field.AGE, Operator.LESS_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.AGE)).thenReturn(6);

        boolean validates = numberCondition.validates(validationInput);

        assertFalse(validates);
    }


}