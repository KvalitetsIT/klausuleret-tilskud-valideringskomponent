package dk.kvalitetsit.klaus.integrationtest;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.Clause;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Objects;

import static dk.kvalitetsit.klaus.integrationtest.MockFactory.clauseDto;
import static dk.kvalitetsit.klaus.integrationtest.MockFactory.dsl;

public class ManagementIT extends BaseTest {

    private final ManagementApi api = new ManagementApi(client);

    @Test
    void testPostClauseDslSet() {

        final List<String> dsl = List.of(MockFactory.dsl);

        try {
            var response = api.v1ClausesDslPost(dsl);
            Assertions.assertEquals(dsl, response);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPostClauseSet() {
        final List<Clause> clauses = List.of(clauseDto);
        try {
            var response = api.v1ClausesPost(clauses);
            Assertions.assertEquals(clauses, response);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteClause() {
        final List<Clause> dsl = List.of(clauseDto);
        try {
            var created = api.v1ClausesPost(dsl);
            System.out.println(created);
            var deleted = api.v1ClausesIdDelete(Objects.requireNonNull(created.getFirst().getUuid()));
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }


}
