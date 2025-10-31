package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class IndicationConditionExpressionTest {

    @Test
    void matches_WhenValueIsNull_ReturnsFalse() {
        var stringCondition = new IndicationConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn(null);

        var validationError = stringCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.Field.INDICATION, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("testValue", conditionError.value());
    }

    @Test
    void matches_WhenValueIsDifferentThanRequired_ReturnsFalse() {
        var stringCondition = new IndicationConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn("different value");

        var validationError = stringCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.Field.INDICATION, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("testValue", conditionError.value());
    }

    @Test
    void matches_WhenValueIsEqualToRequired_ReturnsTrue() {
        var stringCondition = new IndicationConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn("testValue");

        boolean validates = stringCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }
}