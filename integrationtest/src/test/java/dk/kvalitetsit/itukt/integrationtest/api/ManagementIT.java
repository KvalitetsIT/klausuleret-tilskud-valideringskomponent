package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.ClauseInput;

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
                .expression(expression);

        api.call20250801clausesPost(clauseInput);
        var clauses = api.call20250801clausesGet();

        assertEquals(1, clauses.size(), "Expected the same number of clauses as were created");
        var clause = clauses.getFirst();
        assertThat(clause)
                .usingRecursiveComparison()
                .ignoringFields("uuid")
                .withFailMessage("The clauses read is expected to match the clauses created")
                .isEqualTo(clauseInput);
    }


}
