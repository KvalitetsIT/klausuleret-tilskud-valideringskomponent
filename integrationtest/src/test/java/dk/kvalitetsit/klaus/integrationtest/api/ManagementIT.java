package dk.kvalitetsit.klaus.integrationtest.api;

import dk.kvalitetsit.klaus.integrationtest.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.Condition;
import org.openapitools.client.model.DslInput;
import org.openapitools.client.model.Operator;

import java.util.List;

import static dk.kvalitetsit.klaus.integrationtest.MockFactory.clauseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


public class ManagementIT extends BaseTest {

    private ManagementApi api;

    @BeforeEach
    void setup() {
        this.api = new ManagementApi(client);
    }


    @Test
    void testPostAndGetClauseDsl() {
        var dslInput = new DslInput().dsl("Klausul CHOL: (ALDER >= 13)");

        api.call20250801clausesDslPost(dslInput);
        var clauses = api.call20250801clausesGet();

        assertEquals(1, clauses.size());
        var clause = clauses.getFirst();
        assertEquals("CHOL", clause.getName());
        assertInstanceOf(Condition.class, clause.getExpression());
        var condition = (Condition) clause.getExpression();
        assertEquals("ALDER", condition.getField());
        assertEquals(List.of("13"), condition.getValues());
        assertEquals(Operator.GREATER_THAN_OR_EQUAL_TO, condition.getOperator());
    }

    @Test
    void testPostAndGetClause() {
        api.call20250801clausesPost(clauseDto);
        var clauses = api.call20250801clausesGet();

        assertEquals(1, clauses.size());
        var clause = clauses.getFirst();
        assertEquals(clauseDto.getName(), clause.getName());
        assertEquals(clauseDto.getExpression(), clause.getExpression());
    }

}
