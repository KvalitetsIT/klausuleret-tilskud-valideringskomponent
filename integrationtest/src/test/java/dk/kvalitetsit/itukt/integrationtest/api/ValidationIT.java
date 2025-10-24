package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.management.repository.entity.Field;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.*;

import java.time.OffsetDateTime;
import java.util.List;

import static dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.AND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ValidationIT extends BaseTest {

    // Matches hardcoded value in cache
    private static final String VALID_INDICATION = "313", INVALID_INDICATION = "390";
    private static final long DRUG_ID = 28103139399L;
    private static ValidationApi validationApi;

    @Override
    protected void load(ClauseRepository repository) {
        // Hardcoded clause for phase 1
        var ageAndIndication = new ExpressionEntity.BinaryExpressionEntity(
                new ExpressionEntity.NumberConditionEntity(Field.AGE, Operator.GREATER_THAN, 50),
                AND,
                new ExpressionEntity.StringConditionEntity(Field.INDICATION, "313"));

        var existingDrugMedication = new ExpressionEntity.ExistingDrugMedicationConditionEntity(1L, "ATC123", "*", "*");
        var expression = new ExpressionEntity.BinaryExpressionEntity(
                ageAndIndication,
                BinaryExpression.Operator.OR,
                existingDrugMedication
        );
        var clause = new ClauseForCreation("KRINI", expression, "message");

        repository.create(clause);
    }

    @BeforeEach
    void setup() {
        validationApi = new ValidationApi(client);
    }

    @Test
    void call20250801validatePost_WithoutExistingDrugMedicationWithInputThatMatchesClauseAndValidatesAgeAndIndication_ReturnsSuccess() {
        // Matches hardcoded value in cache
        String elementPath = "path";
        int age = 51;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(elementPath, age, VALID_INDICATION, null);

        var response = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void call20250801validatePost_WithoutExistingDrugMedicationWhenItIsRequired_ReturnsValidationNotPossible() {
        String elementPath = "path";
        int age = 20;  // Hardcoded clause in cache requires age > 50 or existing drug medication
        var request = createValidationRequest(elementPath, age, VALID_INDICATION, null);

        var response = validationApi.call20250801validatePost(request);

        var validationNotPossible = assertInstanceOf(ValidationNotPossible.class, response,
                "Validation should not be possible when existing drug medication is required but not provided");
        assertEquals(ValidationNotPossible.ReasonEnum.EXISTING_DRUG_MEDICATIONS_REQUIRED, validationNotPossible.getReason(),
                "Reason should be that existing drug medications are required");
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndValidatesExistingDrugMedication_ReturnsSuccess() {
        int age = 1;  // Hardcoded clause in cache requires age > 50
        var existingDrugMedication = new ExistingDrugMedication()
                .drugIdentifier(0L)
                .atcCode("ATC123") // Matches hardcoded clause
                .formCode("anything") // Hardcoded clause has wildcard for form
                .routeOfAdministrationCode("anything"); // Hardcoded clause has wildcard for route of administration code
        var request = createValidationRequest("path", age, VALID_INDICATION, List.of(existingDrugMedication));

        var response = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsValidation_ReturnsValidationError() {
        String elementPath = "path";
        int age = 50;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(elementPath, age, VALID_INDICATION, List.of());
        var response = validationApi.call20250801validatePost(request);

        var failedResponse = assertInstanceOf(ValidationFailed.class, response);
        assertEquals(1, failedResponse.getValidationErrors().size());
        var validationError = failedResponse.getValidationErrors().getFirst();
        String expectedClauseCode = "KRINI"; // Hardcoded clause code in stamdata cache
        assertEquals(expectedClauseCode, validationError.getClauseCode());
        String expectedErrorMessage = "message";
        assertEquals(expectedErrorMessage, validationError.getErrorMessage());
        assertEquals(elementPath, validationError.getElementPath());
        int expectedErrorCode = 10800; // Hardcoded error code in clause cache
        assertEquals(expectedErrorCode, validationError.getErrorCode());
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsIndicationValidation_ReturnsValidationError() {
        String elementPath = "path";
        int age = 51;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(elementPath, age, INVALID_INDICATION, List.of());

        var response = validationApi.call20250801validatePost(request);

        var failedResponse = assertInstanceOf(ValidationFailed.class, response);
        assertEquals(1, failedResponse.getValidationErrors().size());
        var validationError = failedResponse.getValidationErrors().getFirst();
        String expectedClauseCode = "KRINI"; // Hardcoded clause code in stamdata cache
        String expectedErrorMessage = "message";
        assertEquals(expectedClauseCode, validationError.getClauseCode());
        assertEquals(expectedErrorMessage, validationError.getErrorMessage());
        assertEquals(elementPath, validationError.getElementPath());
    }

    @Test
    void call20250801validatePost_WithInputThatFailsValidationButErrorCodeSkipped_ReturnsSuccess() {
        String elementPath = "path";
        int age = 20;  // Hardcoded clauses in cache requires age > 50 or existing drug medication
        var request = createValidationRequest(elementPath, age, INVALID_INDICATION, List.of())
                .addSkipValidationsItem(10800); // Hardcoded error code in clause cache

        var successfulResponse = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, successfulResponse, "Validation should succeed when error code is skipped");
    }

    @Test
    void call20250801validatePost_WithoutRequiredExistingDrugMedicationButErrorCodeSkipped_ReturnsSuccess() {
        String elementPath = "path";
        int age = 20;  // Hardcoded clauses in cache requires age > 50 or existing drug medication
        var request = createValidationRequest(elementPath, age, VALID_INDICATION, null)
                .addSkipValidationsItem(10800); // Hardcoded error code in clause cache

        var successfulResponse = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, successfulResponse, "Validation should succeed when error code is skipped");
    }

    private ValidationRequest createValidationRequest(String elementPath, int age, String indication, List<ExistingDrugMedication> existingDrugMedication) {
        Validate validate = createValidateElement(elementPath, indication);
        return new ValidationRequest()
                .age(age)
                .personIdentifier("1234567890")
                .addValidateItem(validate)
                .existingDrugMedications(existingDrugMedication);
    }

    private Validate createValidateElement(String path, String indication) {
        NewDrugMedication newDrugMedication = createNewDrugMedication(indication);
        return new Validate()
                .action(Validate.ActionEnum.CREATE_DRUG_MEDICATION)
                .elementPath(path)
                .newDrugMedication(newDrugMedication);
    }

    private NewDrugMedication createNewDrugMedication(String indication) {
        return new NewDrugMedication()
                .drugIdentifier(DRUG_ID)
                .indicationCode(indication)
                .createdBy(createActor())
                .reportedBy(createActor())
                .createdDateTime(OffsetDateTime.now());
    }

    private static Actor createActor() {
        return new Actor()
                .identifier("actor1")
                .organisationSpeciality("")
                .specialityCode("");
    }
}
