package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.mapping.ErrorMapper;
import dk.kvalitetsit.itukt.validation.mapping.ValidationRequestInputMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceAdaptorTest {

    @InjectMocks
    private ValidationServiceAdaptor validationServiceAdaptor;

    @Mock
    private ValidationServiceImpl validationService;

    @Mock
    private ValidationRequestInputMapper validationRequestInputMapper;

    @Mock
    private ErrorMapper errorMapper;

    @BeforeEach
    void setUp() {
        validationServiceAdaptor = new ValidationServiceAdaptor(validationService, validationRequestInputMapper, errorMapper);
    }

    @Test
    void validate_WithRequestWithoutValidateElements_ReturnsSuccess() {
        var request = new ValidationRequest();
        Mockito.when(validationRequestInputMapper.map(request)).thenReturn(List.of());

        var response = validationServiceAdaptor.validate(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void validate_ValidatesWithMappedInput() {
        var request = Mockito.mock(ValidationRequest.class);
        Mockito.when(validationRequestInputMapper.map(request)).thenReturn(List.of(
                Mockito.mock(ValidationInput.class),
                Mockito.mock(ValidationInput.class)
        ));
        ValidationInput expectedValidationInput1 = Mockito.mock();
        ValidationInput expectedValidationInput2 = Mockito.mock();

        Mockito.when(validationService.validate(Mockito.any(ValidationInput.class))).thenReturn(List.of());
        Mockito.when(validationRequestInputMapper.map(request)).thenReturn(List.of(expectedValidationInput1, expectedValidationInput2));

        validationServiceAdaptor.validate(request);

        Mockito.verify(validationService, Mockito.times(1)).validate(expectedValidationInput1);
        Mockito.verify(validationService, Mockito.times(1)).validate(expectedValidationInput2);
    }

    @Test
    void validate_WhenAllValidationSucceeds_ReturnsSuccess() {
        var request = Mockito.mock(ValidationRequest.class);
        Mockito.when(validationRequestInputMapper.map(request)).thenReturn(List.of(
                Mockito.mock(ValidationInput.class),
                Mockito.mock(ValidationInput.class)
        ));
        Mockito.when(validationService.validate(Mockito.any())).thenReturn(List.of());

        ValidationResponse response = validationServiceAdaptor.validate(request);

        Mockito.verify(validationService, Mockito.times(2)).validate(Mockito.any());
        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void validate_WhenTwoOutOfThreeValidationsFail_ReturnsFailureWithAllValidationErrors() {
        var request = Mockito.mock(ValidationRequest.class);
        var validationInput1 = Mockito.mock(ValidationInput.class);
        var validationInput2 = Mockito.mock(ValidationInput.class);
        var validationInput3 = Mockito.mock(ValidationInput.class);
        Mockito.when(validationRequestInputMapper.map(request))
                .thenReturn(List.of(validationInput1, validationInput2, validationInput3));
        var validationError1 = Mockito.mock(dk.kvalitetsit.itukt.validation.service.model.ValidationError.class);
        var validationError2 = Mockito.mock(dk.kvalitetsit.itukt.validation.service.model.ValidationError.class);
        var mappedError1 = Mockito.mock(org.openapitools.model.ValidationError.class);
        var mappedError2 = Mockito.mock(org.openapitools.model.ValidationError.class);
        Mockito.when(errorMapper.map(validationError1)).thenReturn(mappedError1);
        Mockito.when(errorMapper.map(validationError2)).thenReturn(mappedError2);

        Mockito.when(validationService.validate(validationInput1)).thenReturn(List.of(validationError1));
        Mockito.when(validationService.validate(validationInput2)).thenReturn(List.of(validationError2));
        Mockito.when(validationService.validate(validationInput3)).thenReturn(List.of());

        var response = validationServiceAdaptor.validate(request);

        Mockito.verify(validationService, Mockito.times(3)).validate(Mockito.any(ValidationInput.class));
        assertInstanceOf(ValidationFailed.class, response);
        var failedResponse = (ValidationFailed) response;
        assertEquals(2, failedResponse.getValidationErrors().size());
        assertTrue(failedResponse.getValidationErrors().contains(mappedError1));
        assertTrue(failedResponse.getValidationErrors().contains(mappedError2));
    }

    @Test
    void validate_WhenExistingDrugMedicationsRequiredExceptionIsThrown_ReturnsValidationNotPossible() {
        var request = Mockito.mock(ValidationRequest.class);
        Mockito.when(validationRequestInputMapper.map(request)).thenReturn(List.of(Mockito.mock(ValidationInput.class)));
        Mockito.when(validationService.validate(Mockito.any()))
                .thenThrow(ExistingDrugMedicationRequiredException.class);

        ValidationResponse response = validationServiceAdaptor.validate(request);

        var notPossibleResponse = assertInstanceOf(ValidationNotPossible.class, response,
                "ValidationNotPossible expected when ExistingDrugMedicationRequiredException is thrown");
        assertEquals(ValidationNotPossible.ReasonEnum.EXISTING_DRUG_MEDICATIONS_REQUIRED, notPossibleResponse.getReason(),
                "Reason should be that existing drug medications are required");
    }
}