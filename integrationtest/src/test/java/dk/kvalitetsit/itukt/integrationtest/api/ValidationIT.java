package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.common.model.ClauseField;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.*;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ValidationIT extends BaseTest {

    private static ValidationApi validationApi;
    // Matches hardcoded value in cache
    private static final String VALID_INDICATION = "313", INVALID_INDICATION = "390";

    @Override
    protected void load(ClauseRepository<ClauseEntity> repository) {
        // Hardcoded clause for phase 1
        var clause = new ClauseEntity(
                "KRINI",
                new ExpressionEntity.BinaryExpressionEntity(
                        new ExpressionEntity.ConditionEntity(ClauseField.AGE.name(), Operator.GREATER_THAN, List.of("50")),
                        Expression.BinaryExpression.BinaryOperator.AND,
                        new ExpressionEntity.ConditionEntity(ClauseField.INDICATION.name(), Operator.EQUAL, List.of("313"))
                )
        );
        repository.create(clause);
    }

    @BeforeAll
    void setup() {
        validationApi = new ValidationApi(client);
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndValidates_ReturnsSuccess() {
        long drugId = 28103139399L; // Matches value in stamdata database with clause code = "KRINI"
        String elementPath = "path";
        int age = 51;  // Hardcoded clause in cache requires age > 50
        ValidationRequest request = createValidationRequest(drugId, elementPath, age, VALID_INDICATION);

        var response = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsValidation_ReturnsValidationError() {
        long drugId = 28103139399L; // Matches value in stamdata database with clause code = "KRINI"
        String elementPath = "path";
        int age = 50;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(drugId, elementPath, age, VALID_INDICATION);
        var response = validationApi.call20250801validatePost(request);

        var failedResponse = assertInstanceOf(ValidationFailed.class, response);
        assertEquals(1, failedResponse.getValidationErrors().size());
        var validationError = failedResponse.getValidationErrors().getFirst();
        String expectedClauseCode = "KRINI"; // Hardcoded clause code in stamdata cache
        assertEquals(expectedClauseCode, validationError.getClauseCode());
        assertEquals(elementPath, validationError.getElementPath());
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsIndicationValidation_ReturnsValidationError() {
        long drugId = 28103139399L; // Matches value in stamdata database with clause code = "KRINI"
        String elementPath = "path";
        int age = 51;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(drugId, elementPath, age, INVALID_INDICATION);

        var response = validationApi.call20250801validatePost(request);

        var failedResponse = assertInstanceOf(ValidationFailed.class, response);
        assertEquals(1, failedResponse.getValidationErrors().size());
        var validationError = failedResponse.getValidationErrors().getFirst();
        String expectedClauseCode = "KRINI"; // Hardcoded clause code in stamdata cache
        assertEquals(expectedClauseCode, validationError.getClauseCode());
        assertEquals(elementPath, validationError.getElementPath());
    }

    private ValidationRequest createValidationRequest(long drugId, String elementPath, int age, String indication) {
        Validate validate = createValidateElement(drugId, elementPath, indication);
        return new ValidationRequest()
                .age(age)
                .personIdentifier("1234567890")
                .addValidateItem(validate);
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
