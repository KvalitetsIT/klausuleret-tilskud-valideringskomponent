package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.*;
import org.openapitools.client.model.Error;

import static dk.kvalitetsit.itukt.integrationtest.MockFactory.CLAUSE_1_INPUT;
import static dk.kvalitetsit.itukt.integrationtest.MockFactory.CLAUSE_1_OUTPUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testPostAndGetClauseDsl() {
        var dsl = MockFactory.CLAUSE_1_DSL_INPUT;

        api.call20250801clausesDslPost(dsl);
        var clauses = api.call20250801clausesGet();

        assertEquals(1, clauses.size());
        assertThat(clauses.getFirst())
                .usingRecursiveComparison()
                .ignoringFields("uuid")
                .isEqualTo(CLAUSE_1_OUTPUT);
    }

    @Test
    void testPostAndGetClause() {
        api.call20250801clausesPost(CLAUSE_1_INPUT);
        var clauses = api.call20250801clausesGet();

        assertEquals(1, clauses.size());
        var clause = clauses.getFirst();

        assertThat(clause)
                .usingRecursiveComparison()
                .ignoringFields("uuid")
                .isEqualTo(CLAUSE_1_OUTPUT);
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
        var clauses = api.call20250801clausesGet();

        assertEquals(1, clauses.size(), "Expected the same number of clauses as were created");
        var clause = clauses.getFirst();
        assertThat(clause)
                .usingRecursiveComparison()
                .ignoringFields("uuid", "error")
                .withFailMessage("The clauses read is expected to match the clauses created")
                .isEqualTo(clauseInput);

        assertEquals(
                clauseInput.getError(),
                clause.getError(),
                "The returned error message must match the input error string"
        );
    }

    @Test
    void call20250801clausesPost_whenPostingAValidClauseThenRetrieveACorrectlyInterpretedDSL() {

        String error = "blaah";

        String dsl = "Klausul CLAUSE: INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og (EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE = *} eller ALDER >= 13)";

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
                                        .right(new AgeCondition()
                                                .type(ExpressionType.AGE)
                                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                                .value(13)
                                        )
                                )
                        ))
                .error(error);

        ClauseOutput createClauseResponse = api.call20250801clausesPost(clauseInput);

        DslOutput dslOutput = new DslOutput().dsl(dsl).error(error).uuid(createClauseResponse.getUuid());

        var getDslResponse = api.call20250801clausesDslIdGet(createClauseResponse.getUuid());

        assertEquals(dslOutput, getDslResponse, "Expected the retrieved DSL to match the clause previously created");
    }

    @Test
    void call20250801clausesDslPost_whenPostingAValidDSLThenRetrieveACorrectlyInterpretedClause() {
        String error = "blaah";
        String dsl = "Klausul CLAUSE: INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og (EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE = *} eller ALDER >= 13)";
        DslInput dslInput = new DslInput().dsl(dsl).error(error);

        var createDslResponse = api.call20250801clausesDslPost(dslInput);

        DslOutput dslOutput = new DslOutput().dsl(dsl).error(error).uuid(createDslResponse.getUuid());
        assertEquals(dslOutput.getDsl(), createDslResponse.getDsl(), "Expected the input dsl to match the dsl in the response");

        ClauseOutput clauseOutput = new ClauseOutput().name("CLAUSE").expression(new BinaryExpression()
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
                                        .right(new AgeCondition()
                                                .type(ExpressionType.AGE)
                                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                                .value(13)
                                        )
                                )
                        ))
                .error(error)
                .uuid(dslOutput.getUuid());

        var getClauseResponse = api.call20250801clausesIdGet(dslOutput.getUuid());
        assertEquals(clauseOutput, getClauseResponse, "Expected the clause to match the dsl initially created");

    }

}
