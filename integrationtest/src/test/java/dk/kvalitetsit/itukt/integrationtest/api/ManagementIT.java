package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.common.model.DrugMedication;
import dk.kvalitetsit.itukt.common.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.common.repository.entity.SkippedValidationEntity;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.*;
import dk.kvalitetsit.itukt.integrationtest.repository.stamdata.entity.Pakning;
import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.ExpressionRepositoryImpl;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepositoryImpl;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DepartmentEntity;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DrugClauseView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.*;
import org.springframework.core.io.Resource;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static dk.kvalitetsit.itukt.integrationtest.MockFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ManagementIT extends BaseTest {
    private static final ManagementApi api = new ManagementApi(client);

    private static final ClauseRepository clauseRepository = new ClauseRepositoryImpl(appDatabase.getDatasource(), new ExpressionRepositoryImpl(appDatabase.getDatasource()));
    private static final SkippedValidationRepository skippedValidationRepository = new SkippedValidationRepositoryImpl(appDatabase.getDatasource());

    @Test
    void testGetClauseHistory_ForNonDraft() {
        var expression = new AgeCondition().type("AgeCondition").operator(Operator.EQUAL).value(20);
        var draftClause = api.management20250801ClausesPost(new ClauseInput().name("blaaaaah").error("error1").expression(expression));
        var clause1 = api.management20250801ClausesDraftsIdStatusPut(
                draftClause.getUuid(),
                new DraftClauseStatusInput().status(DraftClauseStatusInput.StatusEnum.ACTIVE).resetSkippedValidations(false));
        var clause2 = api.management20250801ClausesNameStatusPut(
                draftClause.getName(),
                new ClauseStatusInput().status(ClauseStatusInput.StatusEnum.INACTIVE));
        var clause3 = api.management20250801ClausesNameStatusPut(
                draftClause.getName(),
                new ClauseStatusInput().status(ClauseStatusInput.StatusEnum.ACTIVE));

        List<DslOutput> clauses = api.management20250801ClausesDslIdHistoryGet(clause3.getUuid());

        assertEquals(3, clauses.size());
        assertEquals(clause3, clauses.get(0));
        assertEquals(clause2, clauses.get(1));
        assertEquals(clause1, clauses.get(2));
    }

    @Test
    void testGetClauseHistory_ForDraft() {
        var input = new DslInput().name("blaaaaah").dsl("ALDER = 1").error("error1");
        var draft1 = api.management20250801ClausesDslPost(input);
        var draft2 = api.management20250801ClausesDraftsNamePut(
                input.getName(),
                new DslUpdateInput().dsl("ALDER = 2").error("error2"));
        var draft3 = api.management20250801ClausesDraftsNamePut(
                input.getName(),
                new DslUpdateInput().dsl("ALDER = 3").error("error3"));
        var draft4 = api.management20250801ClausesDraftsNamePut(
                input.getName(),
                new DslUpdateInput().dsl("ALDER = 4").error("error4"));

        List<DslOutput> clauses = api.management20250801ClausesDslIdHistoryGet(draft4.getUuid());

        assertEquals(4, clauses.size());
        assertEquals(draft4, clauses.get(0));
        assertEquals(draft3, clauses.get(1));
        assertEquals(draft2, clauses.get(2));
        assertEquals(draft1, clauses.get(3));
    }

    @Test
    void testGetHistoryReturnsEmptyListIfClauseDoesNotExist() {
        var history = api.management20250801ClausesDslIdHistoryGet(UUID.randomUUID());

        assertTrue(history.isEmpty(), "Expected history to be empty for a non-existent clause");
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
    void postClause_WithUnknownDepartmentSpeciality_ThrowsException() {
        var input = new DslInput()
                .name("test")
                .dsl("AFDELINGSSPECIALE = NOT_KNOWN")
                .error("error");
        var e = assertThrows(HttpClientErrorException.BadRequest.class, () -> api.management20250801ClausesDslPost(input));
        assertTrue(e.getMessage().contains("Ukendt afdelingsspeciale NOT_KNOWN"));
    }

    @Test
    void postClause_WithUnknownFormCode_ThrowsException() {
        var input = new DslInput()
                .name("test")
                .dsl("EKSISTERENDE_LÆGEMIDDEL = {FORM = NOT_KNOWN}")
                .error("error");
        var e = assertThrows(HttpClientErrorException.BadRequest.class, () -> api.management20250801ClausesDslPost(input));
        assertTrue(e.getMessage().contains("Ukendt formkode NOT_KNOWN"));
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
    void testDslPostGetCsv() throws IOException {
        var input = new DslInput().name("TEST").dsl("ALDER = 1").error("error");
        api.management20250801ClausesDslPost(input);

        Resource csv = api.management20250801ClausesDslCsvGet(ClauseStatus.DRAFT);

        assertTrue(csv.exists());
        String[] lines = csv.getContentAsString(Charset.defaultCharset()).split("\n");
        assertEquals(2, lines.length);
        assertEquals("name;status;dsl;error;createdBy;createdTime", lines[0]);
        assertTrue(lines[1].contains("TEST;DRAFT;ALDER = 1;error;" + USER_ID));
    }

    @Test
    void testDslPostPutAndGet() {
        var input = new DslInput().name("test").dsl("ALDER = 1").error("error");
        api.management20250801ClausesDslPost(input);
        var updateInput = new DslUpdateInput().dsl("ALDER = 55").error("updated error");
        var updateOutput = api.management20250801ClausesDraftsNamePut(input.getName(), updateInput);
        var drafts = api.management20250801ClausesDslGet(ClauseStatus.DRAFT);

        var expected = new DslOutput()
                .name(input.getName())
                .dsl(updateInput.getDsl())
                .error(updateInput.getError())
                .status(ClauseStatus.DRAFT)
                .createdBy(USER_ID);
        assertThat(updateOutput)
                .usingRecursiveComparison()
                .ignoringFields("uuid", "createdTime")
                .isEqualTo(expected);
        assertEquals(1, drafts.size());
        assertEquals(updateOutput, drafts.getFirst());
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
                MockFactory.createExistingDrugMedicationCondition("atc1", FORM_CODE, "adm1"),
                MockFactory.createExistingDrugMedicationCondition("atc2", FORM_CODE, "adm2"));
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
                                                .formCode(FORM_CODE)
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
                                                .formCode(FORM_CODE)
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
        var updateInput = new DslUpdateInput().dsl("alder=0").error("updated error");
        var updatedClause = api.management20250801ClausesDraftsNamePut(clauseCreated.getName(), updateInput);

        api.management20250801ClausesIdDelete(updatedClause.getUuid());

        var initialClauseAfterDeletion = clauseRepository.read(clauseCreated.getUuid());
        var updatedClauseAfterDeletion = clauseRepository.read(updatedClause.getUuid());
        var draftsAfterDeletion = api.management20250801ClausesDslGet(ClauseStatus.DRAFT);

        assertTrue(initialClauseAfterDeletion.isEmpty(), "Expected the created clause to be deleted");
        assertTrue(updatedClauseAfterDeletion.isEmpty(), "Expected the updated clause to be deleted");
        assertTrue(draftsAfterDeletion.isEmpty(), "Expected no more drafts after deletion");
    }

    @Test
    void testGetDrugCount_ForClauseWithOneDrug_Returns1() {
        String clauseName = setupStamdataClauseWithOneDrug();
        restartService();

        var drugCount = api.management20250801ClausesNameDrugCountGet(clauseName);

        assertNotNull(drugCount);
        assertEquals(1, drugCount.getDrugCount());
    }

    @Test
    void testGetDrugCount_ForClauseWithNoDrugs_Returns0() {
        var drugCount = api.management20250801ClausesNameDrugCountGet("HEST");

        assertNotNull(drugCount);
        assertEquals(0, drugCount.getDrugCount());
    }

    private static String setupStamdataClauseWithOneDrug() {
        String clauseName = "TEST";
        var stamdataDatasource = stamDatabase.getDatasource();
        var laegemiddelRepository = new LaegemiddelRepository(stamdataDatasource);
        var pakningRepository = new PakningRepository(stamdataDatasource);
        var klausuleringRepository = new KlausuleringRepository(stamdataDatasource);
        var sorEntityRepository = new SorEntityRepository(stamdataDatasource);
        var formbetegnelseRepository = new FormbetegnelseRepository(stamdataDatasource);

        var inThePast = Date.from(Instant.now().minusSeconds(1));
        var inTheFuture = Date.from(Instant.now().plusSeconds(1000));
        var laegemiddel = new DrugClauseView.Laegemiddel(1L);
        var pakning = new Pakning(laegemiddel.DrugId(), clauseName, 1L);
        var klausulering = new DrugClauseView.Klausulering(clauseName, "test");
        var department = new DepartmentEntity("1", "2", DEPARTMENT_SPECIALITY, "", "", "", "", "", "", "");
        DrugMedication.Form form = new DrugMedication.Form(FORM_CODE);
        laegemiddelRepository.insert(laegemiddel, inThePast, inTheFuture);
        pakningRepository.insert(pakning, inThePast, inTheFuture);
        klausuleringRepository.insert(klausulering, inThePast, inTheFuture);
        sorEntityRepository.insert(department, inThePast, inTheFuture, inThePast, inTheFuture);
        formbetegnelseRepository.insert(form, inThePast, inTheFuture);
        return clauseName;
    }

}
