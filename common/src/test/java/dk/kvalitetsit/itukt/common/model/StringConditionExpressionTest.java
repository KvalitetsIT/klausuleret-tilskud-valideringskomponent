package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringConditionExpressionTest {

    @Test
    void matches_WhenValueIsNotAString_ReturnsFalse() {
        var stringCondition = new StringConditionExpression(Expression.Condition.Field.INDICATION, "testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.INDICATION)).thenReturn(0);

        boolean validates = stringCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WhenValueIsNull_ReturnsFalse() {
        var stringCondition = new StringConditionExpression(Expression.Condition.Field.INDICATION, "testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.INDICATION)).thenReturn(null);

        boolean validates = stringCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WhenValueIsDifferentThanRequired_ReturnsFalse() {
        var stringCondition = new StringConditionExpression(Expression.Condition.Field.INDICATION, "testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.INDICATION)).thenReturn("different value");

        boolean validates = stringCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WhenValueIsEqualToRequired_ReturnsTrue() {
        var stringCondition = new StringConditionExpression(Expression.Condition.Field.INDICATION, "testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.getByField(Expression.Condition.Field.INDICATION)).thenReturn("testValue");

        boolean validates = stringCondition.validates(validationInput);

        assertTrue(validates);

    }

}