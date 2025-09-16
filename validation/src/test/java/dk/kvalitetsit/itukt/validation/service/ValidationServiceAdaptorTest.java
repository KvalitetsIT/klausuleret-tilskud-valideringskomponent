package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceAdaptorTest {
    @InjectMocks
    private ValidationServiceAdaptor validationServiceAdaptor;
    @Mock
    private ValidationService<ValidationInput, ValidationResult> validationService;


    @Test
    void validate_WithRequestWithoutValidateElements_ReturnsSuccess() {
        ValidationRequest request = new ValidationRequest();

        ValidationResponse response = validationServiceAdaptor.validate(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void validate_WhenAllValidationSucceeds_ReturnsSuccess() {
        Validate validate1 = createValidate(1L, "path1");
        Validate validate2 = createValidate(2L, "path2");
        ValidationRequest request = createValidationRequest(10, validate1, validate2);
        Mockito.when(validationService.validate(Mockito.any()))
                .thenReturn(new dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess());

        ValidationResponse response = validationServiceAdaptor.validate(request);

        Mockito.verify(validationService, Mockito.times(2)).validate(Mockito.any());
        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void validate_WhenTwoOutOfThreeValidationsFail_ReturnsFailureWithAllValidationErrors() {
        Validate validate1 = createValidate(1L, "path1");
        Validate validate2 = createValidate(2L, "path2");
        Validate validate3 = createValidate(3L, "path3");
        ValidationRequest request = createValidationRequest(10, validate1, validate2, validate3);
        var validationError1 = new dk.kvalitetsit.itukt.validation.service.model.ValidationError("clause1", "text1", "message1");
        var validationError2 = new dk.kvalitetsit.itukt.validation.service.model.ValidationError("clause2", "text2", "message2");
        Mockito.when(validationService.validate(Mockito.argThat(input -> input != null && input.drugId() == 1L)))
                .thenReturn(validationError1);
        Mockito.when(validationService.validate(Mockito.argThat(input -> input != null && input.drugId() == 2L)))
                .thenReturn(validationError2);
        Mockito.when(validationService.validate(Mockito.argThat(input -> input != null && input.drugId() == 3L)))
                .thenReturn(new dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess());

        ValidationResponse response = validationServiceAdaptor.validate(request);

        Mockito.verify(validationService, Mockito.times(3)).validate(Mockito.any());
        assertInstanceOf(ValidationFailed.class, response);
        var failedResponse = (ValidationFailed) response;
        assertEquals(2, failedResponse.getValidationErrors().size());
        var responseErrors = failedResponse.getValidationErrors().stream()
                .collect(Collectors.toMap(ValidationError::getClauseCode, Function.identity()));
        assertTrue(responseErrors.keySet().containsAll(List.of(validationError1.clauseCode(), validationError2.clauseCode())));
        var responseError1 = responseErrors.get(validationError1.clauseCode());
        assertEquals(validationError1.clauseText(), responseError1.getClauseText());
        assertEquals(validate1.getElementPath(), responseError1.getElementPath());
        var responseError2 = responseErrors.get(validationError2.clauseCode());
        assertEquals(validationError2.clauseText(), responseError2.getClauseText());
        assertEquals(validate2.getElementPath(), responseError2.getElementPath());
    }

    private ValidationRequest createValidationRequest(int age, Validate ... validates) {
        return new ValidationRequest()
                .age(age)
                .validate(Arrays.stream(validates).toList());
    }

    private Validate createValidate(long drugId, String elementPath) {
        return new Validate()
                .newDrugMedication(new NewDrugMedication().drugIdentifier(drugId))
                .elementPath(elementPath);
    }
}