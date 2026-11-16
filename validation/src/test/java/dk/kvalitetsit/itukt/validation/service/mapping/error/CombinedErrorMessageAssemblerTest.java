package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CombinedErrorMessageAssemblerTest {
    @Mock
    private ConditionErrorMessageAssembler conditionAssembler;
    @Mock
    private ExistingDrugMedicationErrorMessageAssembler existingDrugMedicationAssembler;
    @Mock
    private AndErrorMessageAssembler andAssembler;
    @Mock
    private OrErrorMessageAssembler orAssembler;
    @Mock
    private ErrorMessageAssemblerFactory factory;

    private CombinedErrorMessageAssembler combinedErrorMessageAssembler;

    @BeforeEach
    void setUp() {
        Mockito.when(factory.createConditionAssembler()).thenReturn(conditionAssembler);
        Mockito.when(factory.createExistingDrugMedicationAssembler()).thenReturn(existingDrugMedicationAssembler);
        Mockito.when(factory.createAndAssembler(Mockito.any())).thenReturn(andAssembler);
        Mockito.when(factory.createOrAssembler(Mockito.any())).thenReturn(orAssembler);
        combinedErrorMessageAssembler = new CombinedErrorMessageAssembler(factory);
    }

    @Test
    void assemble_WithConditionError_MapsWithConditionAssembler() {
        var conditionError = Mockito.mock(ValidationError.ConditionError.class);
        String expectedErrorMessage = "mapped condition error";
        Mockito.when(conditionAssembler.assemble(conditionError)).thenReturn(expectedErrorMessage);

        String errorMessage = combinedErrorMessageAssembler.assemble(conditionError);

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    void assemble_WithExistingDrugMedicationError_MapsWithExistingDrugMedicationAssembler() {
        var existingDrugMedicationError = Mockito.mock(ValidationError.ExistingDrugMedicationError.class);
        String expectedErrorMessage = "mapped existing drug medication error";
        Mockito.when(existingDrugMedicationAssembler.assemble(existingDrugMedicationError)).thenReturn(expectedErrorMessage);

        String errorMessage = combinedErrorMessageAssembler.assemble(existingDrugMedicationError);

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    void assemble_WithAndError_MapsWithAndErrorAssembler() {
        var andError = Mockito.mock(ValidationError.AndError.class);
        String expectedErrorMessage = "mapped and error";
        Mockito.when(andAssembler.assemble(andError)).thenReturn(expectedErrorMessage);

        String errorMessage = combinedErrorMessageAssembler.assemble(andError);

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    void assemble_WithOrError_MapsWithOrErrorAssembler() {
        var orError = Mockito.mock(ValidationError.OrError.class);
        String expectedErrorMessage = "mapped or error";
        Mockito.when(orAssembler.assemble(orError)).thenReturn(expectedErrorMessage);

        String errorMessage = combinedErrorMessageAssembler.assemble(orError);

        assertEquals(expectedErrorMessage, errorMessage);
    }
}