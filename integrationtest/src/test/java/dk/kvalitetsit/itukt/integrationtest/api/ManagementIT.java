package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.BinaryExpression;
import org.openapitools.client.model.BinaryOperator;
import org.openapitools.client.model.ClauseInput;
import org.openapitools.client.model.ExistingDrugMedicationCondition;

import static dk.kvalitetsit.itukt.integrationtest.MockFactory.CLAUSE_1_INPUT;
import static dk.kvalitetsit.itukt.integrationtest.MockFactory.CLAUSE_1_OUTPUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ManagementIT extends BaseTest {

    private ManagementApi api;

    @BeforeAll
    void setup() {
        this.api = new ManagementApi(client);
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
        var expression = new BinaryExpression()
                .left(new ExistingDrugMedicationCondition()
                        .atcCode("atc1")
                        .formCode("form1")
                        .routeOfAdministrationCode("adm1")
                        .type("ExistingDrugMedicationCondition"))
                .operator(BinaryOperator.AND)
                .right(new ExistingDrugMedicationCondition()
                        .atcCode("atc2")
                        .formCode("form2")
                        .routeOfAdministrationCode("adm2")
                        .type("ExistingDrugMedicationCondition"))
                .type("BinaryExpression");
        var clauseInput = new ClauseInput()
                .name("test")
                .expression(expression);

        api.call20250801clausesPost(clauseInput);
        var clauses = api.call20250801clausesGet();

        assertEquals(1, clauses.size());
        var clause = clauses.getFirst();
        assertThat(clause)
                .usingRecursiveComparison()
                .ignoringFields("uuid")
                .isEqualTo(clauseInput);
    }

}
