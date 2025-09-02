package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.*;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ValidationIT extends BaseTest {

    private ValidationApi validationApi;

    @BeforeAll
    void setup() {
        this.validationApi = new ValidationApi(client);
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndValidates_ReturnsSuccess() {
        long drugId = 1L;// Matches hardcoded value in cache
        String elementPath = "path";
        int age = 51;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(drugId, elementPath, age);

        var response = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsValidation_ReturnsValidationError() {
        long drugId = 1L;// Matches hardcoded value in cache
        String elementPath = "path";
        int age = 50;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(drugId, elementPath, age);

        var response = validationApi.call20250801validatePost(request);

        var failedResponse = assertInstanceOf(ValidationFailed.class, response);
        assertEquals(1, failedResponse.getValidationErrors().size());
        var validationError = failedResponse.getValidationErrors().get(0);
        String expectedClauseCode = "KRINI"; // Hardcoded clause code in stamdata cache
        assertEquals(expectedClauseCode, validationError.getClauseCode());
        assertEquals(elementPath, validationError.getElementPath());
    }

    private ValidationRequest createValidationRequest(long drugId, String elementPath, int age) {
        Validate validate = createValidateElement(drugId, elementPath);
        return new ValidationRequest()
                .age(age)
                .personIdentifier("1234567890")
                .addValidateItem(validate);
    }

    private Validate createValidateElement(long drugId, String path) {
        NewDrugMedication newDrugMedication = createNewDrugMedication(drugId);
        return new Validate()
                .action(Validate.ActionEnum.CREATE_DRUG_MEDICATION)
                .elementPath(path)
                .newDrugMedication(newDrugMedication);
    }

    private NewDrugMedication createNewDrugMedication(long drugId) {
        return new NewDrugMedication()
                .drugIdentifier(drugId)
                .indicationCode("")
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
