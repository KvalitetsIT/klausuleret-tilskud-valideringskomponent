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
class OrErrorMapperTest {
    @Mock
    private ValidationErrorMapper<ValidationError> validationErrorMapper;

    @InjectMocks
    private OrErrorMapper orErrorMapper;

    @Test
    void map_ReturnsCorrespondingErrorString() {
        var leftError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(validationErrorMapper.map(leftError)).thenReturn("left error");
        var rightError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(validationErrorMapper.map(rightError)).thenReturn("right error");
        var orError = new ValidationError.OrError(leftError, rightError);

        String errorString = orErrorMapper.map(orError);

        assertEquals("left error eller right error", errorString);
    }
}