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
class AndErrorMessageAssemblerTest {
    @Mock
    private ErrorMessageAssembler<ValidationError> combinedErrorMessageAssembler;

    @InjectMocks
    private AndErrorMessageAssembler andErrorMessageAssembler;

    @Test
    void assemble_WithConditionErrors_ReturnsCorrespondingErrorString() {
        var leftError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(combinedErrorMessageAssembler.assemble(leftError)).thenReturn("left error");
        var rightError = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(combinedErrorMessageAssembler.assemble(rightError)).thenReturn("right error");
        var andError = new ValidationError.AndError(leftError, rightError);

        String errorString = andErrorMessageAssembler.assemble(andError);

        assertEquals("left error og right error", errorString);
    }

    @Test
    void assemble_WithOrErrors_ReturnsErrorStringWithParentheses() {
        var leftError = Mockito.mock(ValidationError.OrError.class);
        Mockito.when(combinedErrorMessageAssembler.assemble(leftError)).thenReturn("left error");
        var rightError = Mockito.mock(ValidationError.OrError.class);
        Mockito.when(combinedErrorMessageAssembler.assemble(rightError)).thenReturn("right error");
        var andError = new ValidationError.AndError(leftError, rightError);

        String errorString = andErrorMessageAssembler.assemble(andError);

        assertEquals("(left error) og (right error)", errorString);
    }
}