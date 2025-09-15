package dk.kvalitetsit.itukt.integrationtest.api;


import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.ClauseLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.*;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@Import(ClauseLoader.class)
public class ValidationIT extends BaseTest {

    private final String validIndication = "313", // Matches hardcoded value in cache
            invalidIndication = "390";
    private ValidationApi validationApi;

    private static long getDrugId() {
        // Matches hardcoded value in cache
        return 28103139399L;
    }

    @BeforeAll
    void setup() {
        this.validationApi = new ValidationApi(client);
    }

    @Test
    void call20250801validatePost_WithoutExistingDrugMedicationWithInputThatMatchesClauseAndValidatesAgeAndIndication_ReturnsSuccess() {
        long drugId = getDrugId();// Matches hardcoded value in cache
        String elementPath = "path";
        int age = 51;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(drugId, elementPath, age, validIndication, null);

        var response = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void call20250801validatePost_WithoutExistingDrugMedicationWhenItIsRequired_ReturnsValidationNotPossible() {
        long drugId = getDrugId();
        String elementPath = "path";
        int age = 20;  // Hardcoded clause in cache requires age > 50 or existing drug medication
        var request = createValidationRequest(drugId, elementPath, age, validIndication, null);

        var response = validationApi.call20250801validatePost(request);

        var validationNotPossible = assertInstanceOf(ValidationNotPossible.class, response,
                "Validation should not be possible when existing drug medication is required but not provided");
        assertEquals(ValidationNotPossible.ReasonEnum.EXISTING_DRUG_MEDICATIONS_REQUIRED, validationNotPossible.getReason(),
                "Reason should be that existing drug medications are required");
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndValidatesExistingDrugMedication_ReturnsSuccess() {
        long drugId = getDrugId();
        int age = 1;  // Hardcoded clause in cache requires age > 50
        var existingDrugMedication = new ExistingDrugMedication()
                .drugIdentifier(0L)
                .atcCode("ATC123") // Matches hardcoded clause
                .formCode("anything") // Hardcoded clause has wildcard for form
                .routeOfAdministrationCode("anything"); // Hardcoded clause has wildcard for route of administration code
        var request = createValidationRequest(drugId, "path", age, validIndication, List.of(existingDrugMedication));

        var response = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsValidation_ReturnsValidationError() {
        long drugId = getDrugId();
        String elementPath = "path";
        int age = 50;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(drugId, elementPath, age, validIndication, List.of());

        var response = validationApi.call20250801validatePost(request);

        var failedResponse = assertInstanceOf(ValidationFailed.class, response);
        assertEquals(1, failedResponse.getValidationErrors().size());
        var validationError = failedResponse.getValidationErrors().getFirst();
        String expectedClauseCode = "KRINI"; // Hardcoded clause code in stamdata cache
        assertEquals(expectedClauseCode, validationError.getClauseCode());
        assertEquals(elementPath, validationError.getElementPath());
        int expectedErrorCode = 10800; // Hardcoded error code in clause cache
        assertEquals(expectedErrorCode, validationError.getErrorCode());
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsIndicationValidation_ReturnsValidationError() {
        long drugId = getDrugId();
        String elementPath = "path";
        int age = 51;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(drugId, elementPath, age, invalidIndication, List.of());

        var response = validationApi.call20250801validatePost(request);

        var failedResponse = assertInstanceOf(ValidationFailed.class, response);
        assertEquals(1, failedResponse.getValidationErrors().size());
        var validationError = failedResponse.getValidationErrors().getFirst();
        String expectedClauseCode = "KRINI"; // Hardcoded clause code in stamdata cache
        assertEquals(expectedClauseCode, validationError.getClauseCode());
        assertEquals(elementPath, validationError.getElementPath());
    }

    private ValidationRequest createValidationRequest(long drugId, String elementPath, int age, String indication, List<ExistingDrugMedication> existingDrugMedication) {
        Validate validate = createValidateElement(drugId, elementPath, indication);
        return new ValidationRequest()
                .age(age)
                .personIdentifier("1234567890")
                .addValidateItem(validate)
                .existingDrugMedications(existingDrugMedication);
    }

    private Validate createValidateElement(long drugId, String path, String indication) {
        NewDrugMedication newDrugMedication = createNewDrugMedication(drugId, indication);
        return new Validate()
                .action(Validate.ActionEnum.CREATE_DRUG_MEDICATION)
                .elementPath(path)
                .newDrugMedication(newDrugMedication);
    }

    private NewDrugMedication createNewDrugMedication(long drugId, String indication) {
        return new NewDrugMedication()
                .drugIdentifier(drugId)
                .indicationCode(indication)
                .createdBy(createActor())
                .reportedBy(createActor())
                .createdDateTime(OffsetDateTime.now());
    }

    private Actor createActor() {
        return new Actor()
                .organisationSpeciality("")
                .specialityCode("");
    }
}
