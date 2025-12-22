package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AgeConditionExpressionTest {

    @Test
    void matches_WithEqualsConditionWhenValueIsBiggerThanRequired_ReturnsFalse() {
        var numberCondition = new AgeConditionExpression(Operator.EQUAL, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(6);

        var validationError = numberCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.AGE, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("5", conditionError.value());
    }

    @Test
    void matches_WithEqualsConditionWhenValueIsLessThanRequired_ReturnsFalse() {
        var numberCondition = new AgeConditionExpression(Operator.EQUAL, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(4);

        var validationError = numberCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.AGE, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("5", conditionError.value());
    }

    @Test
    void matches_WithEqualsConditionWhenValueIsEqualToRequired_ReturnsTrue() {
        var numberCondition = new AgeConditionExpression(Operator.EQUAL, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(5);

        boolean validates = numberCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }

    @Test
    void matches_WithGreaterThanOrEqualToConditionWhenValueIsLessThanRequired_ReturnsFalse() {
        var numberCondition = new AgeConditionExpression(Operator.GREATER_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(4);

        var validationError = numberCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.AGE, conditionError.field());
        assertEquals(Operator.GREATER_THAN_OR_EQUAL_TO, conditionError.operator());
        assertEquals("5", conditionError.value());
    }

    @Test
    void matches_WithGreaterThanOrEqualToConditionWhenValueIsEqualToRequired_ReturnsTrue() {
        var numberCondition = new AgeConditionExpression(Operator.GREATER_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(5);

        boolean validates = numberCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }

    @Test
    void matches_WithGreaterThanOrEqualToConditionWhenValueIsBiggerThanRequired_ReturnsTrue() {
        var numberCondition = new AgeConditionExpression(Operator.GREATER_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(6);

        boolean validates = numberCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanOrEqualToConditionWhenValueIsLessThanRequired_ReturnsTrue() {
        var numberCondition = new AgeConditionExpression(Operator.LESS_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(4);

        boolean validates = numberCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanOrEqualToConditionWhenValueIsEqualToRequired_ReturnsTrue() {
        var numberCondition = new AgeConditionExpression(Operator.LESS_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(5);

        boolean validates = numberCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanOrEqualToConditionWhenValueIsBiggerThanRequired_ReturnsFalse() {
        var numberCondition = new AgeConditionExpression(Operator.LESS_THAN_OR_EQUAL_TO, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(6);

        var validationError = numberCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.AGE, conditionError.field());
        assertEquals(Operator.LESS_THAN_OR_EQUAL_TO, conditionError.operator());
        assertEquals("5", conditionError.value());
    }

    @Test
    void matches_WithGreaterThanConditionWhenValueIsLessThanRequired_ReturnsFalse() {
        var numberCondition = new AgeConditionExpression(Operator.GREATER_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(4);

        var validationError = numberCondition.validates(validationInput);


        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.AGE, conditionError.field());
        assertEquals(Operator.GREATER_THAN, conditionError.operator());
        assertEquals("5", conditionError.value());
    }

    @Test
    void matches_WithGreaterThanConditionWhenValueIsEqualToRequired_ReturnsFalse() {
        var numberCondition = new AgeConditionExpression(Operator.GREATER_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(5);

        var validationError = numberCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.AGE, conditionError.field());
        assertEquals(Operator.GREATER_THAN, conditionError.operator());
        assertEquals("5", conditionError.value());
    }

    @Test
    void matches_WithGreaterThanConditionWhenValueIsBiggerThanRequired_ReturnsTrue() {
        var numberCondition = new AgeConditionExpression(Operator.GREATER_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(6);

        boolean validates = numberCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanConditionWhenValueIsLessThanRequired_ReturnsTrue() {
        var numberCondition = new AgeConditionExpression(Operator.LESS_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(4);

        boolean validates = numberCondition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }

    @Test
    void matches_WithLessThanConditionWhenValueIsEqualToRequired_ReturnsFalse() {
        var numberCondition = new AgeConditionExpression(Operator.LESS_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(5);

        var validationError = numberCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.AGE, conditionError.field());
        assertEquals(Operator.LESS_THAN, conditionError.operator());
        assertEquals("5", conditionError.value());
    }

    @Test
    void matches_WithLessThanConditionWhenValueIsBiggerThanRequired_ReturnsFalse() {
        var numberCondition = new AgeConditionExpression(Operator.LESS_THAN, 5);
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        Mockito.when(validationInput.citizenAge()).thenReturn(6);

        var validationError = numberCondition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.AGE, conditionError.field());
        assertEquals(Operator.LESS_THAN, conditionError.operator());
        assertEquals("5", conditionError.value());
    }


}