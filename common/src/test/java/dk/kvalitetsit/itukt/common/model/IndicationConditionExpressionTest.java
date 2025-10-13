package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndicationConditionExpressionTest {

    @Test
    void matches_WhenValueIsNull_ReturnsFalse() {
        var stringCondition = new IndicationConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn(null);

        boolean validates = stringCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WhenValueIsDifferentThanRequired_ReturnsFalse() {
        var stringCondition = new IndicationConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn("different value");

        boolean validates = stringCondition.validates(validationInput);

        assertFalse(validates);
    }

    @Test
    void matches_WhenValueIsEqualToRequired_ReturnsTrue() {
        var stringCondition = new IndicationConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn("testValue");

        boolean validates = stringCondition.validates(validationInput);

        assertTrue(validates);
    }
}