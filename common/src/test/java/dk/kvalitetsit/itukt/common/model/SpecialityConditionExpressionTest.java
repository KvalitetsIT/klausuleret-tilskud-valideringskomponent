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
        ValidationInput.CreatedBy createdBy = new ValidationInput.CreatedBy("", Optional.empty());
        Mockito.when(validationInput.createdBy()).thenReturn(createdBy);

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.Field.DOCTOR_SPECIALITY, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("testValue", conditionError.value());
    }

    @Test
    void matches_WhenValueIsDifferentThanRequired_ReturnsFalse() {
        var condition = new DoctorSpecialityConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        ValidationInput.CreatedBy createdBy = new ValidationInput.CreatedBy("", Optional.of("different value"));
        Mockito.when(validationInput.createdBy()).thenReturn(createdBy);

        var validationError = condition.validates(validationInput);

        assertTrue(validationError.isPresent());
        var conditionError = assertInstanceOf(ValidationError.ConditionError.class, validationError.get());
        assertEquals(ValidationError.Field.DOCTOR_SPECIALITY, conditionError.field());
        assertEquals(Operator.EQUAL, conditionError.operator());
        assertEquals("testValue", conditionError.value());
    }

    @Test
    void matches_WhenValueIsEqualToRequired_ReturnsTrue() {
        var condition = new DoctorSpecialityConditionExpression("testValue");
        ValidationInput validationInput = Mockito.mock(ValidationInput.class);
        ValidationInput.CreatedBy createdBy = new ValidationInput.CreatedBy("", Optional.of("testValue"));
        Mockito.when(validationInput.createdBy()).thenReturn(createdBy);

        boolean validates = condition.validates(validationInput).isEmpty();

        assertTrue(validates);
    }
}