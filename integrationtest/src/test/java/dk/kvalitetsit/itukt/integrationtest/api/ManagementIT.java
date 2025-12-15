package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static dk.kvalitetsit.itukt.integrationtest.MockFactory.CLAUSE_1_INPUT;
import static dk.kvalitetsit.itukt.integrationtest.MockFactory.CLAUSE_1_OUTPUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ManagementIT extends BaseTest {

    private static ManagementApi api;

    @BeforeEach
    void setup() {
        api = new ManagementApi(client);
    }

    @Override
    protected void load(ClauseRepository repository) {
        // Load data before component initialization
    }

    @Test
    void testGetClauseHistory() {
        AgeCondition expression = new AgeCondition().type("AgeCondition").operator(Operator.EQUAL).value(20);

        var created = List.of(
                api.call20250801clausesPost(new ClauseInput().name("blaaaaah").error("error1").expression(expression)),
                api.call20250801clausesPost(new ClauseInput().name("blaaaaah").error("error2").expression(expression)),
                api.call20250801clausesPost(new ClauseInput().name("blaaaaah").error("error3").expression(expression))
        );

        List<DslOutput> clauses = api.call20250801clausesDslNameHistoryGet("blaaaaah");

        assertEquals(created.size(), clauses.size());

        for (int i = 0; i < created.size(); i++) {
            var x = created.get(i);
            var y = clauses.get(i);
            assertEquals(x.getError(), y.getError());
            assertEquals(x.getCreatedAt(), y.getCreatedAt());
            if (i != 0) {
                // Assert that the timestamp is greater than the previous
                ClauseOutput prev_x = created.get(i - 1);
                DslOutput prev_y = clauses.get(i - 1);

                assertTrue(
                        x.getCreatedAt().isAfter(prev_x.getCreatedAt()),
                        "The timestamp is expected to be greater than the previous version"
                );
                assertTrue(
                        y.getCreatedAt().isAfter(prev_y.getCreatedAt()),
                        "The timestamp is expected to be greater than the previous version"
                );
            }
        }
    }


    @Test
    void testGetHistoryThrowsNotFoundIfClauseDoesNotExist() {
        var e = assertThrows(
                HttpClientErrorException.NotFound.class,
                () -> api.call20250801clausesDslNameHistoryGet("UNKNOWN_CLAUSE")
        );

        String body = e.getResponseBodyAsString();

        assertTrue(body.contains("\"detailed_error\":\"clause with name 'UNKNOWN_CLAUSE' was not found\""));
    }

    @Test
    void testPostAndGetClauseDsl() {
        var dsl = MockFactory.CLAUSE_1_DSL_INPUT;

        api.call20250801clausesDslPost(dsl);
        var clauses = api.call20250801clausesGet(ClauseStatus.DRAFT);

        assertEquals(1, clauses.size());
        assertThat(clauses.getFirst())
                .usingRecursiveComparison()
                .ignoringFields("uuid", "createdAt")
                .isEqualTo(CLAUSE_1_OUTPUT);
    }

    @Test
    void testPostAndGetClause() {
        api.call20250801clausesPost(CLAUSE_1_INPUT);
        var clauses = api.call20250801clausesGet(ClauseStatus.DRAFT);

        assertEquals(1, clauses.size());
        var clause = clauses.getFirst();

        assertThat(clause)
                .usingRecursiveComparison()
                .ignoringFields("uuid", "createdAt")
                .isEqualTo(CLAUSE_1_OUTPUT);
    }

    @Test
    void testDraftAndApproveExistingClause() {
        var postInput1 = CLAUSE_1_INPUT;
        var postInput2 = postInput1.error("updated error");

        var clause = api.call20250801clausesPost(postInput1);
        api.call20250801clausesIdApprovePatch(clause.getUuid());
        var updatedClause = api.call20250801clausesPost(postInput2);
        api.call20250801clausesIdApprovePatch(updatedClause.getUuid());
        var drafts = api.call20250801clausesGet(ClauseStatus.DRAFT);
        var activeClauses = api.call20250801clausesGet(ClauseStatus.ACTIVE);

        assertTrue(drafts.isEmpty());
        assertEquals(1, activeClauses.size());
        assertEquals(updatedClause, activeClauses.getFirst());
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

        api.call20250801clausesPost(clauseInput);
        var clauses = api.call20250801clausesGet(ClauseStatus.DRAFT);

        assertEquals(1, clauses.size(), "Expected the same number of clauses as were created");
        var clause = clauses.getFirst();
        assertThat(clause)
                .usingRecursiveComparison()
                .ignoringFields("uuid", "createdAt")
                .withFailMessage("The clauses read is expected to match the clauses created")
                .isEqualTo(clauseInput);
    }

    @Test
    void call20250801clausesPost_whenPostingAValidClauseThenRetrieveACorrectlyInterpretedDSL() {

        var error = "blaah";

        String dsl = "Klausul CLAUSE: INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og (EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE = *} eller ALDER >= 13 og (LÆGESPECIALE = læge eller LÆGESPECIALE i [kæbekirurg, ortopædkirurg] og ALDER >= 18))";

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

        ClauseOutput createClauseResponse = api.call20250801clausesPost(clauseInput);

        DslOutput dslOutput = new DslOutput().dsl(dsl).error(error).uuid(createClauseResponse.getUuid()).createdAt(createClauseResponse.getCreatedAt());

        var getDslResponse = api.call20250801clausesDslIdGet(createClauseResponse.getUuid());

        assertEquals(dslOutput, getDslResponse, "Expected the retrieved DSL to match the clause previously created");
    }

    @Test
    void call20250801clausesDslPost_whenPostingAValidDSLThenRetrieveACorrectlyInterpretedClause() {
        var error = "blaah";

        String dsl = "Klausul CLAUSE: INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og (EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE = *} eller ALDER >= 13 og (LÆGESPECIALE = LÆGE eller LÆGESPECIALE i [KÆBEKIRURG, ORTOPÆDKIRURG] og ALDER >= 18))";
        DslInput dslInput = new DslInput().dsl(dsl).error(error);

        var createDslResponse = api.call20250801clausesDslPost(dslInput);

        DslOutput dslOutput = new DslOutput().dsl(dsl).error(error).uuid(createDslResponse.getUuid());
        assertEquals(dslOutput.getDsl(), createDslResponse.getDsl(), "Expected the input dsl to match the dsl in the response");

        var getClauseResponse = api.call20250801clausesIdGet(dslOutput.getUuid());

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
                .createdAt(getClauseResponse.getCreatedAt());

        assertEquals(clauseOutput, getClauseResponse, "Expected the clause to match the dsl initially created");

    }

}
