package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.DslInput;

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

}
