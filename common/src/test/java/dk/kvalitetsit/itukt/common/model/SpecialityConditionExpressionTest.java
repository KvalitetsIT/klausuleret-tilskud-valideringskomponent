package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DoctorSpecialityConditionExpressionTest {

    @Test
    void validates_WhenValueIsEmpty_ReturnsValidationError() {
        var condition = new DoctorSpecialityConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        ValidationInput.Actor createdBy = new ValidationInput.Actor("", Optional.empty(), Optional.empty());
        Mockito.when(validationInput.createdBy()).thenReturn(createdBy);

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.DOCTOR_SPECIALITY, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("testValue", conditionError.value());
    }

    @Test
    void matches_WhenValueIsDifferentThanRequired_ReturnsFalse() {
        var condition = new DoctorSpecialityConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        ValidationInput.Actor createdBy = new ValidationInput.Actor("", Optional.of("different value"), Optional.empty());
        Mockito.when(validationInput.createdBy()).thenReturn(createdBy);

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.ConditionError.Field.DOCTOR_SPECIALITY, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("testValue", conditionError.value());
    }

    @Test
    void matches_WhenValueIsEqualToRequired_ReturnsTrue() {
        var condition = new DoctorSpecialityConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        ValidationInput.Actor createdBy = new ValidationInput.Actor("", Optional.of("testValue"), Optional.empty());
        Mockito.when(validationInput.createdBy()).thenReturn(createdBy);

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }
}