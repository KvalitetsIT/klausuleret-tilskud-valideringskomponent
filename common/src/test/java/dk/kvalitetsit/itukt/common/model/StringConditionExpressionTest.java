package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringConditionExpressionTest {
    @Test
    void matches_WhenValueIsNotAString_ReturnsFalse() {
        var stringCondition = new StringConditionExpression(Expression.Condition.Field.INDICATION, "testValue");

        boolean matches = stringCondition.matches(5);

        assertFalse(matches);
    }

    @Test
    void matches_WhenValueIsNull_ReturnsFalse() {
        var stringCondition = new StringConditionExpression(Expression.Condition.Field.INDICATION, "testValue");

        boolean matches = stringCondition.matches(null);

        assertFalse(matches);
    }

    @Test
    void matches_WhenValueIsDifferentThanRequired_ReturnsFalse() {
        var stringCondition = new StringConditionExpression(Expression.Condition.Field.INDICATION, "testValue");

        boolean matches = stringCondition.matches("differentValue");

        assertFalse(matches);
    }

    @Test
    void matches_WhenValueIsEqualToRequired_ReturnsTrue() {
        var stringCondition = new StringConditionExpression(Expression.Condition.Field.INDICATION, "testValue");

        boolean matches = stringCondition.matches(stringCondition.requiredValue());

        assertTrue(matches);

    }

}