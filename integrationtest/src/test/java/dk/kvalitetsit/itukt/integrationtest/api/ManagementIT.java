package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.common.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.common.repository.entity.SkippedValidationEntity;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.KlausuleringRepository;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.LaegemiddelRepository;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.PakningRepository;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.entity.Pakning;
import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.ExpressionRepositoryImpl;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepositoryImpl;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static dk.kvalitetsit.itukt.integrationtest.MockFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ManagementIT extends BaseTest {
    private static final String CLAUSE_WITH_ONE_DRUG = "TEST";
    private static ManagementApi api;

    private static ClauseRepository clauseRepository;
    private static SkippedValidationRepository skippedValidationRepository;


    @BeforeEach
    void setup() {
        clauseRepository = new ClauseRepositoryImpl(appDatabase.getDatasource(), new ExpressionRepositoryImpl(appDatabase.getDatasource()));
        skippedValidationRepository = new SkippedValidationRepositoryImpl(appDatabase.getDatasource());

        api = new ManagementApi(client);
    }

    @Override
    protected void load(ClauseRepository repository) {
        setupStamdata();
    }

    @Test
    void testGetClauseHistory() {
        AgeCondition expression = new AgeCondition().type("AgeCondition").operator(Operator.EQUAL).value(20);

        var created = List.of(
                api.management20250801ClausesPost(new ClauseInput().name("blaaaaah").error("error1").expression(expression)),
                api.management20250801ClausesPost(new ClauseInput().name("blaaaaah").error("error2").expression(expression)),
                api.management20250801ClausesPost(new ClauseInput().name("blaaaaah").error("error3").expression(expression))
        );
        created.forEach(clause ->
                api.management20250801ClausesDraftsIdStatusPut(
                        clause.getUuid(),
                        new DraftClauseStatusInput().status(DraftClauseStatusInput.StatusEnum.ACTIVE).resetSkippedValidations(false)));

        List<DslOutput> clauses = api.management20250801ClausesDslNameHistoryGet("blaaaaah");

        assertEquals(created.size(), clauses.size());

        for (int i = 0; i < created.size(); i++) {
            var x = created.reversed().get(i);
            var y = clauses.get(i);
            assertEquals(x.getError(), y.getError());
            if (i != 0) {
                // Assert that the timestamp is less than the previous
                DslOutput prev_y = clauses.get(i - 1);

                assertTrue(
                        y.getCreatedTime().isBefore(prev_y.getCreatedTime()),
                        "The timestamp is expected to be less than the previous version"
                );
            }
        }
    }


    @Test
    void testGetHistoryThrowsNotFoundIfClauseDoesNotExist() {
        var e = assertThrows(
                HttpClientErrorException.NotFound.class,
                () -> api.management20250801ClausesDslNameHistoryGet("UNKNOWN_CLAUSE")
        );

        String body = e.getResponseBodyAsString();

        assertTrue(body.contains("\"detailed_error\":\"clause with name 'UNKNOWN_CLAUSE' was not found\""));
    }

    @Test
    void testPostAndGetClauseDsl() {
        api.management20250801ClausesDslPost(CLAUSE_1_DSL_INPUT);
        var clauses = api.management20250801ClausesGet(ClauseStatus.DRAFT);

        assertEquals(1, clauses.size());
        assertNotNull(clauses.getFirst().getCreatedTime());
        assertThat(clauses.getFirst())
                .usingRecursiveComparison()
                .ignoringFields("uuid", "createdTime")
                .isEqualTo(CLAUSE_1_OUTPUT);
    }

    @Test
    void testPostAndGetClause() {
        api.management20250801ClausesPost(CLAUSE_1_INPUT);
        var clauses = api.management20250801ClausesGet(ClauseStatus.DRAFT);

        assertEquals(1, clauses.size());
        var clause = clauses.getFirst();

        assertNotNull(clause.getCreatedTime());
        assertThat(clause)
                .usingRecursiveComparison()
                .ignoringFields("uuid", "createdTime")
                .isEqualTo(CLAUSE_1_OUTPUT);
    }

    @Test
    void testDraftAndApproveExistingClause() {
        var postInput1 = CLAUSE_1_INPUT;
        var postInput2 = postInput1.error("updated error");

        var clause = api.management20250801ClausesPost(postInput1);
        api.management20250801ClausesDraftsIdStatusPut(clause.getUuid(), new DraftClauseStatusInput().status(DraftClauseStatusInput.StatusEnum.ACTIVE).resetSkippedValidations(false));
        var updatedClause = api.management20250801ClausesPost(postInput2);
        api.management20250801ClausesDraftsIdStatusPut(updatedClause.getUuid(), new DraftClauseStatusInput().status(DraftClauseStatusInput.StatusEnum.ACTIVE).resetSkippedValidations(false));
        var drafts = api.management20250801ClausesGet(ClauseStatus.DRAFT);
        var activeClauses = api.management20250801ClausesGet(ClauseStatus.ACTIVE);

        assertTrue(drafts.isEmpty());
        assertEquals(1, activeClauses.size());
        assertThat(activeClauses.getFirst())
                .usingRecursiveComparison()
                .ignoringFields("uuid", "status", "createdTime")
                .isEqualTo(updatedClause);
    }

    @Test
    void testInactivateAndActivate() {
        var clause = api.management20250801ClausesDslPost(CLAUSE_1_DSL_INPUT);
        api.management20250801ClausesDraftsIdStatusPut(clause.getUuid(), new DraftClauseStatusInput().status(DraftClauseStatusInput.StatusEnum.ACTIVE).resetSkippedValidations(false));
        var inactiveClause = api.management20250801ClausesNameStatusPut(clause.getName(), new ClauseStatusInput().status(ClauseStatusInput.StatusEnum.INACTIVE));
        var inactiveClauses = api.management20250801ClausesDslGet(ClauseStatus.INACTIVE);
        var activeClause = api.management20250801ClausesNameStatusPut(clause.getName(), new ClauseStatusInput().status(ClauseStatusInput.StatusEnum.ACTIVE));
        var activeClauses = api.management20250801ClausesDslGet(ClauseStatus.ACTIVE);

        assertThat(inactiveClause)
                .usingRecursiveComparison()
                .ignoringFields("uuid", "status", "createdTime")
                .isEqualTo(clause);
        assertEquals(ClauseStatus.INACTIVE, inactiveClause.getStatus());
        assertEquals(1, inactiveClauses.size());
        assertEquals(inactiveClause, inactiveClauses.getFirst());

        assertThat(activeClause)
                .usingRecursiveComparison()
                .ignoringFields("uuid", "status", "createdTime")
                .isEqualTo(clause);
        assertEquals(ClauseStatus.ACTIVE, activeClause.getStatus());
        assertEquals(1, activeClauses.size());
        assertEquals(activeClause, activeClauses.getFirst());
    }

    @Test
    void testPostAndGetClauseWithExistingDrugMedicationConditions() {
        var expression = MockFactory.createBinaryAndExpression(
                MockFactory.createExistingDrugMedicationCondition("atc1", "form1", "adm1"),
                MockFactory.createExistingDrugMedicationCondition("atc2", "form2", "adm2"));
        var clauseInput = new ClauseInput()
                .name("test")
                .expression(expression)
                .error("message");

        api.management20250801ClausesPost(clauseInput);
        var clauses = api.management20250801ClausesGet(ClauseStatus.DRAFT);

        assertEquals(1, clauses.size(), "Expected the same number of clauses as were created");
        var clause = clauses.getFirst();
        assertThat(clause)
                .usingRecursiveComparison()
                .ignoringFields("uuid", "status", "createdBy", "createdTime")
                .withFailMessage("The clauses read is expected to match the clauses created")
                .isEqualTo(clauseInput);
    }

    @Test
    void management20250801ClausesPost_whenPostingAValidClauseThenRetrieveACorrectlyInterpretedDSL() {

        var error = "blaah";

        String dsl = "INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og (EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE = *} eller ALDER >= 13 og (LÆGESPECIALE = læge eller LÆGESPECIALE i [kæbekirurg, ortopædkirurg] og ALDER >= 18))";

        ClauseInput clauseInput = new ClauseInput().name("CLAUSE").expression(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .operator(BinaryOperator.OR)
                        .left(new IndicationCondition()
                                .type(ExpressionType.INDICATION)
                                .value("C10BA03")
                        )
                        .right(new BinaryExpression()
                                .type(ExpressionType.BINARY)
                                .left(new BinaryExpression()
                                        .type(ExpressionType.BINARY)
                                        .left(new IndicationCondition()
                                                .type(ExpressionType.INDICATION)
                                                .value("C10BA02")
                                        )
                                        .operator(BinaryOperator.OR)
                                        .right(new IndicationCondition()
                                                .type(ExpressionType.INDICATION)
                                                .value("C10BA05")
                                        )
                                )
                                .operator(BinaryOperator.AND)
                                .right(new BinaryExpression()
                                        .type(ExpressionType.BINARY)
                                        .left(new ExistingDrugMedicationCondition()
                                                .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                                .formCode("TABLET")
                                                .routeOfAdministrationCode("*")
                                                .atcCode("*")
                                        )
                                        .operator(BinaryOperator.OR)
                                        .right(new BinaryExpression()
                                                .type(ExpressionType.BINARY)
                                                .left(new AgeCondition()
                                                        .type(ExpressionType.AGE)
                                                        .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                                        .value(13)
                                                )
                                                .operator(BinaryOperator.AND)
                                                .right(new BinaryExpression()
                                                        .type(ExpressionType.BINARY)
                                                        .left(new DoctorSpecialityCondition()
                                                                .type(ExpressionType.DOCTOR_SPECIALITY)
                                                                .value("læge"))
                                                        .operator(BinaryOperator.OR)
                                                        .right(new BinaryExpression()
                                                                .type(ExpressionType.BINARY)
                                                                .left(new BinaryExpression()
                                                                        .type(ExpressionType.BINARY)
                                                                        .left(new DoctorSpecialityCondition()
                                                                                .type(ExpressionType.DOCTOR_SPECIALITY)
                                                                                .value("kæbekirurg"))
                                                                        .operator(BinaryOperator.OR)
                                                                        .right(new DoctorSpecialityCondition()
                                                                                .type(ExpressionType.DOCTOR_SPECIALITY)
                                                                                .value("ortopædkirurg")))
                                                                .operator(BinaryOperator.AND)
                                                                .right(new AgeCondition()
                                                                        .type(ExpressionType.AGE)
                                                                        .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                                                        .value(18)))
                                                )
                                        )
                                )
                        ))
                .error(error);

        ClauseOutput createClauseResponse = api.management20250801ClausesPost(clauseInput);

        DslOutput dslOutput = new DslOutput()
                .name("CLAUSE")
                .dsl(dsl)
                .error(error)
                .uuid(createClauseResponse.getUuid())
                .status(ClauseStatus.DRAFT)
                .createdBy(USER_ID)
                .createdTime(createClauseResponse.getCreatedTime());

        var getDslResponse = api.management20250801ClausesDslIdGet(createClauseResponse.getUuid());

        assertEquals(dslOutput, getDslResponse, "Expected the retrieved DSL to match the clause previously created");
    }

    @Test
    void management20250801ClausesDslPost_whenPostingAValidDSLThenRetrieveACorrectlyInterpretedClause() {
        var error = "blaah";

        String dsl = "INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og (EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE = *} eller ALDER >= 13 og (LÆGESPECIALE = LÆGE eller LÆGESPECIALE i [KÆBEKIRURG, ORTOPÆDKIRURG] og ALDER >= 18))";
        DslInput dslInput = new DslInput().name("CLAUSE").dsl(dsl).error(error);

        var createDslResponse = api.management20250801ClausesDslPost(dslInput);

        DslOutput dslOutput = new DslOutput().dsl(dsl).error(error).uuid(createDslResponse.getUuid());
        assertEquals(dslOutput.getDsl(), createDslResponse.getDsl(), "Expected the input dsl to match the dsl in the response");

        var getClauseResponse = api.management20250801ClausesIdGet(dslOutput.getUuid());

        ClauseOutput clauseOutput = new ClauseOutput().name("CLAUSE").expression(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .left(new IndicationCondition()
                                .type(ExpressionType.INDICATION)
                                .value("C10BA03")
                        )
                        .operator(BinaryOperator.OR)
                        .right(new BinaryExpression()
                                .type(ExpressionType.BINARY)
                                .left(new BinaryExpression()
                                        .type(ExpressionType.BINARY)
                                        .left(new IndicationCondition()
                                                .type(ExpressionType.INDICATION)
                                                .value("C10BA02")
                                        )
                                        .operator(BinaryOperator.OR)
                                        .right(new IndicationCondition()
                                                .type(ExpressionType.INDICATION)
                                                .value("C10BA05")
                                        )
                                )
                                .operator(BinaryOperator.AND)
                                .right(new BinaryExpression()
                                        .type(ExpressionType.BINARY)
                                        .left(new ExistingDrugMedicationCondition()
                                                .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                                .formCode("TABLET")
                                                .routeOfAdministrationCode("*")
                                                .atcCode("*")
                                        )
                                        .operator(BinaryOperator.OR)
                                        .right(new BinaryExpression()
                                                .type(ExpressionType.BINARY)
                                                .left(new AgeCondition()
                                                        .type(ExpressionType.AGE)
                                                        .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                                        .value(13))
                                                .operator(BinaryOperator.AND)
                                                .right(new BinaryExpression()
                                                        .type(ExpressionType.BINARY)
                                                        .left(new DoctorSpecialityCondition()
                                                                .type(ExpressionType.DOCTOR_SPECIALITY)
                                                                .value("LÆGE"))
                                                        .operator(BinaryOperator.OR)
                                                        .right(new BinaryExpression()
                                                                .type(ExpressionType.BINARY)
                                                                .left(new BinaryExpression()
                                                                        .type(ExpressionType.BINARY)
                                                                        .left(new DoctorSpecialityCondition()
                                                                                .type(ExpressionType.DOCTOR_SPECIALITY)
                                                                                .value("KÆBEKIRURG"))
                                                                        .operator(BinaryOperator.OR)
                                                                        .right(new DoctorSpecialityCondition()
                                                                                .type(ExpressionType.DOCTOR_SPECIALITY)
                                                                                .value("ORTOPÆDKIRURG")))
                                                                .operator(BinaryOperator.AND)
                                                                .right(new AgeCondition()
                                                                        .type(ExpressionType.AGE)
                                                                        .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                                                        .value(18))
                                                        ))))))
                .error(error)
                .uuid(dslOutput.getUuid())
                .status(ClauseStatus.DRAFT)
                .createdBy(USER_ID)
                .createdTime(getClauseResponse.getCreatedTime());

        assertEquals(clauseOutput, getClauseResponse, "Expected the clause to match the dsl initially created");

    }

    @Test
    void testApproveAndResetSkippedValidationOfExistingClause() {
        var clauseCreated1 = api.management20250801ClausesPost(CLAUSE_1_INPUT);
        var draftRead1 = clauseRepository.read(clauseCreated1.getUuid()).orElseThrow();
        Assertions.assertEquals(dk.kvalitetsit.itukt.common.model.Clause.Status.DRAFT, draftRead1.status());
        UUID approvedUuid1 = api.management20250801ClausesDraftsIdStatusPut(clauseCreated1.getUuid(), new DraftClauseStatusInput().status(DraftClauseStatusInput.StatusEnum.ACTIVE).resetSkippedValidations(false)).getUuid();
        var activeClause1 = clauseRepository.read(approvedUuid1).orElseThrow();
        Assertions.assertEquals(dk.kvalitetsit.itukt.common.model.Clause.Status.ACTIVE, activeClause1.status());
        SkippedValidationEntity skippedValidation1 = new SkippedValidationEntity(activeClause1.id(), "blaah", "blaaaaah");
        skippedValidationRepository.create(List.of(skippedValidation1));
        Assertions.assertTrue(skippedValidationRepository.exists(skippedValidation1));

        var clauseCreated2 = api.management20250801ClausesPost(CLAUSE_1_INPUT);
        var draftRead2 = clauseRepository.read(clauseCreated2.getUuid()).orElseThrow();
        Assertions.assertEquals(dk.kvalitetsit.itukt.common.model.Clause.Status.DRAFT, draftRead2.status());
        UUID approvedUuid2 = api.management20250801ClausesDraftsIdStatusPut(clauseCreated2.getUuid(), new DraftClauseStatusInput().status(DraftClauseStatusInput.StatusEnum.ACTIVE).resetSkippedValidations(false)).getUuid();
        var activeClause2 = clauseRepository.read(approvedUuid2).orElseThrow();
        Assertions.assertEquals(dk.kvalitetsit.itukt.common.model.Clause.Status.ACTIVE, activeClause2.status());
        SkippedValidationEntity skippedValidation2 = new SkippedValidationEntity(activeClause2.id(), skippedValidation1.actorId(), skippedValidation1.personId());
        Assertions.assertTrue(skippedValidationRepository.exists(skippedValidation2), "The entry is expected to exist since the 'resetSKippedValidations' flag was set to false and therefore the entries are supposed to be copied from the original clause");

        var clauseCreated3 = api.management20250801ClausesPost(CLAUSE_1_INPUT);
        var draftRead3 = clauseRepository.read(clauseCreated3.getUuid()).orElseThrow();
        Assertions.assertEquals(dk.kvalitetsit.itukt.common.model.Clause.Status.DRAFT, draftRead3.status());
        UUID approvedUuid3 = api.management20250801ClausesDraftsIdStatusPut(clauseCreated3.getUuid(), new DraftClauseStatusInput().status(DraftClauseStatusInput.StatusEnum.ACTIVE).resetSkippedValidations(true)).getUuid();
        var activeClause3 = clauseRepository.read(approvedUuid3).orElseThrow();
        Assertions.assertEquals(dk.kvalitetsit.itukt.common.model.Clause.Status.ACTIVE, activeClause3.status());
        SkippedValidationEntity skippedValidation3 = new SkippedValidationEntity(activeClause3.id(), skippedValidation1.actorId(), skippedValidation1.personId());
        Assertions.assertFalse(skippedValidationRepository.exists(skippedValidation3), "The entry is not expected to exist since the 'resetSKippedValidations' flag was set to true and therefore the entries are not supposed to be copied from the original clause");

    }

    @Test
    void testDeleteClause(){
        var clauseCreated = api.management20250801ClausesPost(CLAUSE_1_INPUT);
        var clauseDeleted = api.management20250801ClausesIdDelete(clauseCreated.getUuid());
        var noClause = clauseRepository.read(clauseCreated.getUuid());

        assertEquals(clauseCreated, clauseDeleted, "Expected the created clause to be deleted");
        assertTrue(noClause.isEmpty(), "Expected the created clause to be deleted");
    }

    @Test
    void testGetDrugCount_ForClauseWithOneDrug_Returns1() {
        var drugCount = api.management20250801ClausesNameDrugCountGet(CLAUSE_WITH_ONE_DRUG);

        assertNotNull(drugCount);
        assertEquals(1, drugCount.getDrugCount());
    }

    @Test
    void testGetDrugCount_ForClauseWithNoDrugs_Returns0() {
        var drugCount = api.management20250801ClausesNameDrugCountGet("HEST");

        assertNotNull(drugCount);
        assertEquals(0, drugCount.getDrugCount());
    }

    private static void setupStamdata() {
        var stamdataDatasource = stamDatabase.getDatasource();
        var laegemiddelRepository = new LaegemiddelRepository(stamdataDatasource);
        var pakningRepository = new PakningRepository(stamdataDatasource);
        var klausuleringRepository = new KlausuleringRepository(stamdataDatasource);

        var inThePast = Date.from(Instant.now().minusSeconds(1));
        var inTheFuture = Date.from(Instant.now().plusSeconds(1000));
        var laegemiddel = new DrugClauseView.Laegemiddel(1L);
        var pakning = new Pakning(laegemiddel.DrugId(), CLAUSE_WITH_ONE_DRUG, 1L);
        var klausulering = new DrugClauseView.Klausulering(CLAUSE_WITH_ONE_DRUG, "test");
        laegemiddelRepository.insert(laegemiddel, inThePast, inTheFuture);
        pakningRepository.insert(pakning, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering, inThePast, inTheFuture);
    }

}
