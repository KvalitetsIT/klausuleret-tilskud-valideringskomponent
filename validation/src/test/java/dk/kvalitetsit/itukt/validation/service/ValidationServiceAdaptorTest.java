package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;
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

import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceAdaptorTest {
    @InjectMocks
    private ValidationServiceAdaptor validationServiceAdaptor;
    @Mock
    private ValidationService<ValidationInput, List<ValidationError>> validationService;


    @Test
    void validate_WithRequestWithoutValidateElements_ReturnsSuccess() {
        ValidationRequest request = new ValidationRequest();

        ValidationResponse response = validationServiceAdaptor.validate(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void validate_ValidatesWithMappedInput() {
        String creator1 = "creator1";
        String reporter1 = "reporter1";
        Validate validate1 = createValidate(1L, "1111", "path1", creator1, reporter1);
        String creator2 = "creator2";
        String reporter2 = "reporter2";
        Validate validate2 = createValidate(2L, "2222", "path2", creator2, reporter2);
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication(1L, "atc", "form", "adm");
        ValidationRequest request = createValidationRequest(List.of(1), 10, List.of(existingDrugMedication), validate1, validate2);
        Mockito.when(validationService.validate(Mockito.any())).thenReturn(List.of());

        validationServiceAdaptor.validate(request);

        var expectedExistingDrugMedication = new dk.kvalitetsit.itukt.common.model.ExistingDrugMedication(existingDrugMedication.getAtcCode(), existingDrugMedication.getFormCode(), existingDrugMedication.getRouteOfAdministrationCode());
        ValidationInput expectedValidationInput1 = new ValidationInput(request.getPersonIdentifier(), creator1, of(reporter1), request.getSkipValidations(), request.getAge(), validate1.getNewDrugMedication().getDrugIdentifier(), validate1.getNewDrugMedication().getIndicationCode(), of(List.of(expectedExistingDrugMedication)));
        ValidationInput expectedValidationInput2 = new ValidationInput(request.getPersonIdentifier(), creator2, of(reporter2), request.getSkipValidations(), request.getAge(), validate2.getNewDrugMedication().getDrugIdentifier(), validate2.getNewDrugMedication().getIndicationCode(), of(List.of(expectedExistingDrugMedication)));
        Mockito.verify(validationService).validate(Mockito.eq(expectedValidationInput1));
        Mockito.verify(validationService).validate(Mockito.eq(expectedValidationInput2));
    }

    @Test
    void validate_WhenAllValidationSucceeds_ReturnsSuccess() {
        Validate validate1 = createValidate(1L, "1234", "path1", "creator1", "reporter1");
        Validate validate2 = createValidate(2L, "1234", "path2", "creator2", "reporter2");
        ValidationRequest request = createValidationRequest(List.of(1), 10, List.of(), validate1, validate2);
        Mockito.when(validationService.validate(Mockito.any())).thenReturn(List.of());

        ValidationResponse response = validationServiceAdaptor.validate(request);

        Mockito.verify(validationService, Mockito.times(2)).validate(Mockito.any());
        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void validate_WhenTwoOutOfThreeValidationsFail_ReturnsFailureWithAllValidationErrors() {
        Validate validate1 = createValidate(1L, "1234", "path1", "creator1", "reporter1");
        Validate validate2 = createValidate(2L, "1234", "path2", "creator2", "reporter2");
        Validate validate3 = createValidate(3L, "1234", "path3", "creator3", "reporter3");
        ValidationRequest request = createValidationRequest(List.of(1), 10, List.of(), validate1, validate2, validate3);
        var validationError1 = new dk.kvalitetsit.itukt.validation.service.model.ValidationError(new ValidationError.Clause("clause1", "text1", "message1"), "specific error", 1);
        var validationError2 = new dk.kvalitetsit.itukt.validation.service.model.ValidationError(new ValidationError.Clause("clause2", "text2", "message2"), "specific error", 2);
        Mockito.when(validationService.validate(Mockito.argThat(input -> input != null && input.drugId() == 1L)))
                .thenReturn(List.of(validationError1));
        Mockito.when(validationService.validate(Mockito.argThat(input -> input != null && input.drugId() == 2L)))
                .thenReturn(List.of(validationError2));
        Mockito.when(validationService.validate(Mockito.argThat(input -> input != null && input.drugId() == 3L)))
                .thenReturn(List.of());

        ValidationResponse response = validationServiceAdaptor.validate(request);

        Mockito.verify(validationService, Mockito.times(3)).validate(Mockito.any());
        assertInstanceOf(ValidationFailed.class, response);
        var failedResponse = (ValidationFailed) response;
        assertEquals(2, failedResponse.getValidationErrors().size());
        var responseErrors = failedResponse.getValidationErrors().stream().collect(Collectors.toMap(error -> error.getClause().getCode(), Function.identity()));

        assertTrue(responseErrors.keySet().containsAll(List.of(validationError1.clause().code(), validationError2.clause().code())));
        var responseError1 = responseErrors.get(validationError1.clause().code());
        assertEquals(validationError1.clause().text(), responseError1.getClause().getText());
        assertEquals(validate1.getElementPath(), responseError1.getElementPath());
        var responseError2 = responseErrors.get(validationError2.clause().code());
        assertEquals(validationError2.clause().text(), responseError2.getClause().getText());
        assertEquals(validate2.getElementPath(), responseError2.getElementPath());
        assertEquals(validationError1.code(), responseError1.getCode());
        assertEquals(validationError2.code(), responseError2.getCode());
        assertEquals(validationError1.message(), responseError1.getMessage());
        assertEquals(validationError2.message(), responseError2.getMessage());
    }

    @Test
    void validate_WhenExistingDrugMedicationsRequiredExceptionIsThrown_ReturnsValidationNotPossible() {
        Validate validate = createValidate(1L, "1234", "path1", "creator1", "reporter1");
        ValidationRequest request = createValidationRequest(List.of(1), 10, List.of(), validate);
        Mockito.when(validationService.validate(Mockito.any()))
                .thenThrow(ExistingDrugMedicationRequiredException.class);

        ValidationResponse response = validationServiceAdaptor.validate(request);

        var notPossibleResponse = assertInstanceOf(ValidationNotPossible.class, response,
                "ValidationNotPossible expected when ExistingDrugMedicationRequiredException is thrown");
        assertEquals(ValidationNotPossible.ReasonEnum.EXISTING_DRUG_MEDICATIONS_REQUIRED, notPossibleResponse.getReason(),
                "Reason should be that existing drug medications are required");
    }

    private ValidationRequest createValidationRequest(List<Integer> skippedValidations, int age, List<ExistingDrugMedication> existingDrugMedication, Validate ... validates) {
        return new ValidationRequest()
                .personIdentifier("1234")
                .skipValidations(skippedValidations)
                .age(age)
                .existingDrugMedications(existingDrugMedication)
                .validate(Arrays.stream(validates).toList());
    }

    private Validate createValidate(long drugId, String indicationCode, String elementPath, String createdBy, String reportedBy) {
        return new Validate()
                .newDrugMedication(new NewDrugMedication()
                        .createdBy(new Actor().identifier(createdBy))
                        .reportedBy(new Actor().identifier(reportedBy))
                        .drugIdentifier(drugId)
                        .indicationCode(indicationCode))
                .elementPath(elementPath);
    }
}