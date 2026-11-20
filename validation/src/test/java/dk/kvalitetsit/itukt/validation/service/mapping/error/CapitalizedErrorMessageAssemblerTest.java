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
class CapitalizedErrorMessageAssemblerTest {
    @Mock
    private ErrorMessageAssembler<ValidationError> delegate;

    @InjectMocks
    private CapitalizedErrorMessageAssembler assembler;

    @Test
    void assemble_WithLowercaseFirstLetter_ReturnsCapitalizedErrorMessage() {
        ValidationError error = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(delegate.assemble(error)).thenReturn("error message");

        String errorString = assembler.assemble(error);

        assertEquals("Error message", errorString);
    }

    @Test
    void assemble_WithEmptyMessage_ReturnsEmptyString() {
        ValidationError error = Mockito.mock(ValidationError.ConditionError.class);
        Mockito.when(delegate.assemble(error)).thenReturn("");

        String errorString = assembler.assemble(error);

        assertEquals("", errorString);
    }
}