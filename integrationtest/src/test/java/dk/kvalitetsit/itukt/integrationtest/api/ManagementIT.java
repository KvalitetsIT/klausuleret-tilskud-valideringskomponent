package dk.kvalitetsit.itukt.integrationtest.api;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.DslInput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static dk.kvalitetsit.itukt.integrationtest.MockFactory.CLAUSE_1_DTO;


public class ManagementIT extends BaseTest {

    private ManagementApi api;

    @BeforeEach
    void setup() {
        this.api = new ManagementApi(client);
    }


    @Test
    void testPostAndGetClauseDsl() {
        var dsl = new DslInput().dsl(MockFactory.CLAUSE_1_DSL);

        api.call20250801clausesDslPost(dsl);
        var clauses = api.call20250801clausesGet();

        assertEquals(1, clauses.size());
        assertEquals(CLAUSE_1_DTO.uuid(clauses.getFirst().getUuid()), clauses.getFirst());
    }

    @Test
    void testPostAndGetClause() {
        api.call20250801clausesPost(CLAUSE_1_DTO);
        var clauses = api.call20250801clausesGet();

        assertEquals(1, clauses.size());
        var clause = clauses.getFirst();
        assertEquals(CLAUSE_1_DTO.uuid(clause.getUuid()), clause);
    }

}
