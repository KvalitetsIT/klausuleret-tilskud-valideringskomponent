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
class CombinedValidationErrorMapperTest {
    @Mock
    private ConditionErrorMapper conditionErrorMapper;
    @Mock
    private ExistingDrugMedicationErrorMapper existingDrugMedicationErrorMapper;
    @Mock
    private AndErrorMapper andErrorMapper;
    @Mock
    private OrErrorMapper orErrorMapper;
    @Mock
    private ValidationErrorMapperFactory factory;

    private CombinedValidationErrorMapper combinedValidationErrorMapper;

    @BeforeEach
    void setUp() {
        Mockito.when(factory.createConditionErrorMapper()).thenReturn(conditionErrorMapper);
        Mockito.when(factory.createExistingDrugMedicationErrorMapper()).thenReturn(existingDrugMedicationErrorMapper);
        Mockito.when(factory.createAndErrorMapper(Mockito.any())).thenReturn(andErrorMapper);
        Mockito.when(factory.createOrErrorMapper(Mockito.any())).thenReturn(orErrorMapper);
        combinedValidationErrorMapper = new CombinedValidationErrorMapper(factory);
    }

    @Test
    void map_WithConditionError_MapsWithConditionErrorMapper() {
        var conditionError = Mockito.mock(ValidationError.ConditionError.class);
        String expectedErrorMessage = "mapped condition error";
        Mockito.when(conditionErrorMapper.map(conditionError)).thenReturn(expectedErrorMessage);

        String errorMessage = combinedValidationErrorMapper.map(conditionError);

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    void map_WithExistingDrugMedicationError_MapsWithExistingDrugMedicationErrorMapper() {
        var existingDrugMedicationError = Mockito.mock(ValidationError.ExistingDrugMedicationError.class);
        String expectedErrorMessage = "mapped existing drug medication error";
        Mockito.when(existingDrugMedicationErrorMapper.map(existingDrugMedicationError)).thenReturn(expectedErrorMessage);

        String errorMessage = combinedValidationErrorMapper.map(existingDrugMedicationError);

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    void map_WithAndError_MapsWithAndErrorMapper() {
        var andError = Mockito.mock(ValidationError.AndError.class);
        String expectedErrorMessage = "mapped and error";
        Mockito.when(andErrorMapper.map(andError)).thenReturn(expectedErrorMessage);

        String errorMessage = combinedValidationErrorMapper.map(andError);

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    void map_WithOrError_MapsWithOrErrorMapper() {
        var orError = Mockito.mock(ValidationError.OrError.class);
        String expectedErrorMessage = "mapped or error";
        Mockito.when(orErrorMapper.map(orError)).thenReturn(expectedErrorMessage);

        String errorMessage = combinedValidationErrorMapper.map(orError);

        assertEquals(expectedErrorMessage, errorMessage);
    }
}