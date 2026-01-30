package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class IndicationConditionExpressionTest {

    @Test
    void validates_WhenValueIsNull_ReturnsValidationError() {
        var stringCondition = new IndicationConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn(null);

        var validationError = stringCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.INDICATION, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("testValue", conditionError.value());
    }

    @Test
    void validates_WhenValueIsDifferentThanRequired_ReturnsValidationError() {
        var stringCondition = new IndicationConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn("different value");

        var validationError = stringCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.INDICATION, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("testValue", conditionError.value());
    }

    @Test
    void validates_WhenValueIsEqualToRequired_ReturnsNoErrors() {
        var stringCondition = new IndicationConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn("testValue");

        boolean validates = stringCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }

    @Test
    void validates_WhenValueIsEqualToRequiredButDifferentCase_ReturnsNoErrors() {
        var stringCondition = new IndicationConditionExpression("test");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.indicationCode()).thenReturn("TeSt");

        boolean validates = stringCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }
}