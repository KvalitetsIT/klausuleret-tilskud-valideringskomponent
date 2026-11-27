package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.PrescriptionValidationError;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PrefixedErrorMessageAssemblerTest {
    @Mock
    private ErrorMessageAssembler<ValidationError> errorMessageAssembler;

    @InjectMocks
    private PrefixedErrorMessageAssembler prefixedErrorMessageAssembler;

    @Test
    void assemble_ReturnsPrefixedErrorMessage() {
        var error = new PrescriptionValidationError("drug name", Mockito.mock(ValidationError.ConditionError.class));
        String errorMessage = "error message";
        Mockito.when(errorMessageAssembler.assemble(error.content())).thenReturn(errorMessage);

        String prefixedErrorMessage = prefixedErrorMessageAssembler.assemble(error);

        String expectedMessage = "Manglende opfyldelse af tilskudsklausulen for '%s': %s".formatted(error.drugName(), errorMessage);
        assertEquals(expectedMessage, prefixedErrorMessage);
    }
}