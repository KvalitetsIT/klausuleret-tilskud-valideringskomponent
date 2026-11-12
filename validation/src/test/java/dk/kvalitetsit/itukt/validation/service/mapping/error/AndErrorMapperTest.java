package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AndErrorMapperTest {
    @Mock
    private ValidationErrorMapper<ValidationError> validationErrorMapper;

    @InjectMocks
    private AndErrorMapper andErrorMapper;

    @Test
    void map_WithConditionErrors_ReturnsCorrespondingErrorString() {
        var leftError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(validationErrorMapper.map(leftError)).thenReturn("left error");
        var rightError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(validationErrorMapper.map(rightError)).thenReturn("right error");
        var andError = new ValidationError.AndError(leftError, rightError);

        String errorString = andErrorMapper.map(andError);

        assertEquals("left error og right error", errorString);
    }

    @Test
    void map_WithOrErrors_ReturnsErrorStringWithParentheses() {
        var leftError = Mockito.mock(ValidationError.OrError.class);
        Mockito.when(validationErrorMapper.map(leftError)).thenReturn("left error");
        var rightError = Mockito.mock(ValidationError.OrError.class);
        Mockito.when(validationErrorMapper.map(rightError)).thenReturn("right error");
        var andError = new ValidationError.AndError(leftError, rightError);

        String errorString = andErrorMapper.map(andError);

        assertEquals("(left error) og (right error)", errorString);
    }
}