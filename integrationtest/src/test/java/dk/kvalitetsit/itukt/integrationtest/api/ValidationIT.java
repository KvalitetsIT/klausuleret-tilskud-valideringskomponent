package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Field;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ValidationApi;
import org.openapitools.client.model.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.AND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ValidationIT extends BaseTest {

    // Matches hardcoded value in cache
    private static final long DRUG_ID = 28103139399L;
    private static final String CLAUSE_NAME = "KRINI";
    private static final String CLAUSE_TEXT = "Kronisk Rhinitis";
    private static final String CLAUSE_ERROR_MESSAGE = "message";
    private static final String ELEMENT_PATH = "path";

    private static final int VALID_AGE = 51;
    private static final String VALID_INDICATION = "313", INVALID_INDICATION = "390";
    private static final String VALID_DEPARTMENT_ID = "1000241000016008"; // <- Points to a department in the DB with "infektionsmedicin" as speciality
    private static final String VALID_DOCTOR_SPECIALITY = "ortopædkirurg";
    private static final String VALID_ATC = "ATC123";
    private static ValidationApi validationApi;

    @Override
    protected void load(ClauseRepository repository) {
        // Hardcoded clause for phase 1
        var ageAndIndication = new ExpressionEntity.BinaryExpressionEntity(
                new ExpressionEntity.NumberConditionEntity(Field.AGE, Operator.GREATER_THAN, 50),
                AND,
                new ExpressionEntity.StringConditionEntity(Field.INDICATION, VALID_INDICATION));

        // The value "infektionsmedicin" in the StringConditionEntity below has to match one of the specialties assigned to the SOR/SHAK specified in the request
        var departmentSpecialityRequirement = new ExpressionEntity.StringConditionEntity(Field.DEPARTMENT_SPECIALITY, "infektionsmedicin");

        var existingDrugMedication = new ExpressionEntity.ExistingDrugMedicationConditionEntity(1L, VALID_ATC, "*", "*");
        var orExpression = new ExpressionEntity.BinaryExpressionEntity(
                ageAndIndication,
                BinaryExpression.Operator.OR,
                existingDrugMedication
        );

        var createdByExpression = new ExpressionEntity.StringConditionEntity(Field.DOCTOR_SPECIALITY, VALID_DOCTOR_SPECIALITY);
        ExpressionEntity.BinaryExpressionEntity specialityConditions = new ExpressionEntity.BinaryExpressionEntity(2L, createdByExpression, AND, departmentSpecialityRequirement);
        var expression = new ExpressionEntity.BinaryExpressionEntity(orExpression, AND, specialityConditions);
        var clause = new ClauseInput(CLAUSE_NAME, expression, CLAUSE_ERROR_MESSAGE);

        UUID uuid = repository.create(clause).uuid();
        repository.updateDraftToActive(uuid);
    }

    @BeforeEach
    void setup() {
        validationApi = new ValidationApi(client);
    }

    @Test
    void call20250801validatePost_WithoutExistingDrugMedicationWithInputThatMatchesClauseAndValidatesAgeAndIndication_ReturnsSuccess() {
        var request = createValidationRequest(VALID_AGE, VALID_INDICATION, null, VALID_DOCTOR_SPECIALITY, VALID_DEPARTMENT_ID);

        var response = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void call20250801validatePost_WithoutExistingDrugMedicationWhenItIsRequired_ReturnsValidationNotPossible() {
        int age = 20;  // Hardcoded clause in cache requires age > 50 or existing drug medication
        var request = createValidationRequest(age, VALID_INDICATION, null, VALID_DOCTOR_SPECIALITY, VALID_DEPARTMENT_ID);

        var response = validationApi.call20250801validatePost(request);

        var validationNotPossible = assertInstanceOf(ValidationNotPossible.class, response,
                "Validation should not be possible when existing drug medication is required but not provided");
        assertEquals(ValidationNotPossible.ReasonEnum.EXISTING_DRUG_MEDICATIONS_REQUIRED, validationNotPossible.getReason(),
                "Reason should be that existing drug medications are required");
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndValidatesExistingDrugMedication_ReturnsSuccess() {
        int age = 1;  // Hardcoded clause in cache requires age > 50
        var existingDrugMedication = new ExistingDrugMedicationInput()
                .drugIdentifier(0L)
                .atcCode(VALID_ATC)
                .formCode("anything") // Hardcoded clause has wildcard for form
                .routeOfAdministrationCode("anything"); // Hardcoded clause has wildcard for route of administration code
        var request = createValidationRequest(age, VALID_INDICATION, List.of(existingDrugMedication), VALID_DOCTOR_SPECIALITY, VALID_DEPARTMENT_ID);

        var response = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsValidation_ReturnsValidationError() {
        int age = 50;  // Hardcoded clause in cache requires age > 50
        var request = createValidationRequest(age, VALID_INDICATION, List.of(), VALID_DOCTOR_SPECIALITY, VALID_DEPARTMENT_ID);
        var response = validationApi.call20250801validatePost(request);
        assertValidationError(response, "alder skal være større end 50 eller tidligere medicinsk behandling med følgende påkrævet: ATC = ATC123, Formkode = *, Administrationsrutekode = *");
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsIndicationValidation_ReturnsValidationError() {
        var request = createValidationRequest(VALID_AGE, INVALID_INDICATION, List.of(), VALID_DOCTOR_SPECIALITY, VALID_DEPARTMENT_ID);
        var response = validationApi.call20250801validatePost(request);
        assertValidationError(response, "indikation skal være 313 eller tidligere medicinsk behandling med følgende påkrævet: ATC = ATC123, Formkode = *, Administrationsrutekode = *");
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsDoctorSpecialityValidation_ReturnsValidationError() {
        var request = createValidationRequest(VALID_AGE, VALID_INDICATION, List.of(), "invalid speciale", VALID_DEPARTMENT_ID);
        var response = validationApi.call20250801validatePost(request);
        assertValidationError(response, "lægespeciale skal være " + VALID_DOCTOR_SPECIALITY);
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndValidatesReportedByValidation_ReturnsSuccess() {
        var request = createValidationRequest(VALID_AGE, VALID_INDICATION, List.of(), "invalid speciale", VALID_DOCTOR_SPECIALITY, VALID_DEPARTMENT_ID);
        var response = validationApi.call20250801validatePost(request);
        assertInstanceOf(ValidationSuccess.class, response);
    }

    @Test
    void call20250801validatePost_WithInputThatFailsValidationButErrorCodeSkipped_ReturnsSuccess() {
        int age = 20;  // Hardcoded clauses in cache requires age > 50 or existing drug medication
        var request = createValidationRequest(age, INVALID_INDICATION, List.of(), VALID_DOCTOR_SPECIALITY, VALID_DEPARTMENT_ID)
                .addSkipValidationsItem(10800); // Hardcoded error code in clause cache

        var successfulResponse = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, successfulResponse, "Validation should succeed when error code is skipped");
    }

    @Test
    void call20250801validatePost_WithoutRequiredExistingDrugMedicationButErrorCodeSkipped_ReturnsSuccess() {
        int age = 20;  // Hardcoded clauses in cache requires age > 50 or existing drug medication
        var request = createValidationRequest(age, VALID_INDICATION, null, VALID_DOCTOR_SPECIALITY, VALID_DEPARTMENT_ID)
                .addSkipValidationsItem(10800); // Hardcoded error code in clause cache

        var successfulResponse = validationApi.call20250801validatePost(request);

        assertInstanceOf(ValidationSuccess.class, successfulResponse, "Validation should succeed when error code is skipped");
    }

    @Test
    void call20250801validatePost_WithInputThatMatchesClauseAndFailsDepartmentSpecialityValidation_ReturnsValidationError() {
        String invalidDepartmentId = "222231000016004"; // Points to a department in the DB with "radiologi" as speciality. "infektionsmedicin" is required.
        var request = createValidationRequest(VALID_AGE, VALID_INDICATION, List.of(), VALID_DOCTOR_SPECIALITY, invalidDepartmentId);
        var response = validationApi.call20250801validatePost(request);
        assertValidationError(response, "afdelingens speciale skal være infektionsmedicin");
    }

    private static void assertValidationError(ValidationResponse response, String expectedErrorMessage) {
        var failedResponse = assertInstanceOf(ValidationFailed.class, response);
        assertEquals(1, failedResponse.getValidationErrors().size());
        var validationError = failedResponse.getValidationErrors().getFirst();
        ValidationError expectedValidationError = new ValidationError()
                .elementPath(ELEMENT_PATH)
                .message(expectedErrorMessage)
                .code(10800)
                .clause(new Clause()
                        .code(CLAUSE_NAME)
                        .text(CLAUSE_TEXT)
                        .message(CLAUSE_ERROR_MESSAGE));
        assertEquals(expectedValidationError, validationError);
    }

    private ValidationRequest createValidationRequest(int age, String indication, List<ExistingDrugMedicationInput> existingDrugMedication, String doctorSpeciality, String departmentSorId) {
        return createValidationRequest(age, indication, existingDrugMedication, doctorSpeciality, "", departmentSorId);
    }

    private ValidationRequest createValidationRequest(int age, String indication, List<ExistingDrugMedicationInput> existingDrugMedication, String doctorSpeciality, String reportedByDoctorSpeciality, String departmentSorId) {
        Validate validate = createValidateElement(indication, doctorSpeciality, reportedByDoctorSpeciality, departmentSorId);
        return new ValidationRequest()
                .age(age)
                .personIdentifier("1234567890")
                .addValidateItem(validate)
                .existingDrugMedications(existingDrugMedication);
    }

    private Validate createValidateElement(String indication, String doctorSpeciality, String reportedByDoctorSpeciality, String departmentSorId) {
        NewDrugMedication newDrugMedication = createNewDrugMedication(indication, doctorSpeciality, reportedByDoctorSpeciality, departmentSorId);
        return new Validate()
                .action(Validate.ActionEnum.CREATE_DRUG_MEDICATION)
                .elementPath(ELEMENT_PATH)
                .newDrugMedication(newDrugMedication);
    }

    private NewDrugMedication createNewDrugMedication(String indication, String doctorSpeciality, String reportedByDoctorSpeciality, String departmentSorId) {
        return new NewDrugMedication()
                .drugIdentifier(DRUG_ID)
                .indicationCode(indication)
                .createdBy(createActor(doctorSpeciality, departmentSorId))
                .reportedBy(createActor(reportedByDoctorSpeciality, departmentSorId))
                .createdDateTime(OffsetDateTime.now());
    }

    private static Actor createActor(String speciality, String departmentSorId) {
        return new Actor()
                .identifier("actor1")
                .specialityCode(speciality)
                .departmentIdentifier(
                        new DepartmentIdentifier().code(departmentSorId)
                                .type(DepartmentIdentifier.TypeEnum.SOR)
                );
    }
}