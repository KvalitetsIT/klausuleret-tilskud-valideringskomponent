package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.management.exceptions.ExpressionValidationException;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExpressionValidationExceptionMapperTest {
    @Mock
    private Mapper<ExpressionValidationError, String> expressionValidationErrorMapper;

    @InjectMocks
    private ExpressionValidationExceptionMapper expressionValidationExceptionMapper;

    @Test
    void map_MapsValidationErrors() {
        var error1 = Mockito.mock(UnknownValueError.class);
        var error2 = Mockito.mock(UnknownValueError.class);
        var exception = new ExpressionValidationException(List.of(error1, error2));
        String errorMessage1 = "Error 1";
        String errorMessage2 = "Error 2";
        Mockito.when(expressionValidationErrorMapper.map(error1)).thenReturn(errorMessage1);
        Mockito.when(expressionValidationErrorMapper.map(error2)).thenReturn(errorMessage2);

        var mappedException = expressionValidationExceptionMapper.map(exception);

        assertEquals(errorMessage1 + "\n" + errorMessage2, mappedException.getMessage());
    }
}