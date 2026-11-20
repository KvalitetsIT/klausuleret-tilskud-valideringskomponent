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
class OrErrorMessageAssemblerTest {
    @Mock
    private ErrorMessageAssembler<ValidationError> combinedErrorMessageAssembler;

    @InjectMocks
    private OrErrorMessageAssembler orErrorMessageAssembler;

    @Test
    void assemble_ReturnsCorrespondingErrorString() {
        var leftError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(combinedErrorMessageAssembler.assemble(leftError)).thenReturn("left error");
        var rightError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(combinedErrorMessageAssembler.assemble(rightError)).thenReturn("right error");
        var orError = new ValidationError.OrError(leftError, rightError);

        String errorString = orErrorMessageAssembler.assemble(orError);

        assertEquals("left error eller right error", errorString);
    }
}