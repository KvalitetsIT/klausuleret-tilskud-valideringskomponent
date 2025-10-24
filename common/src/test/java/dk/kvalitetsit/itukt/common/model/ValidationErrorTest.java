package dk.kvalitetsit.itukt.common.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ValidationErrorTest {

    @Test
    void conditionErrorToErrorString_WithEqualsOperator_ReturnsCorrespondingErrorString() {
        var conditionError = new ValidationError.ConditionError(ValidationError.Field.AGE, Operator.EQUAL, "0");

        String errorString = conditionError.toErrorString();

        assertEquals("alder skal være 0", errorString);
    }

    @Test
    void andErrorToErrorString_ReturnsCorrespondingErrorString() {
        var leftError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(leftError.toErrorString()).thenReturn("left error");
        var rightError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(rightError.toErrorString()).thenReturn("right error");
        var andError = new ValidationError.AndError(leftError, rightError);

        String errorString = andError.toErrorString();

        assertEquals("left error og right error", errorString);
    }
}