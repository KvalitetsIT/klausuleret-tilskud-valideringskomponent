package dk.kvalitetsit.klaus.integrationtest;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.Clause;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Objects;

import static dk.kvalitetsit.klaus.integrationtest.MockFactory.clauseDto;

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
            response.forEach(clause -> Assertions.assertNotNull(clause.getUuid()));
            response.forEach(clause -> Assertions.assertNotNull(clause.getVersion()));

            Assertions.assertEquals(clauses.stream().map(Clause::getExpression).toList(), response.stream().map(Clause::getExpression).toList());
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteClause() {
        final List<Clause> dsl = List.of(clauseDto);
        try {
            var created = api.v1ClausesPost(dsl);
            var deleted = api.v1ClausesIdDelete(Objects.requireNonNull(created.getFirst().getUuid()));
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPostClauseDoIncrementVersion() {
        final List<Clause> dsl = List.of(clauseDto);
        try {
            var first = api.v1ClausesPost(dsl);
            var second = api.v1ClausesPost(dsl);
            var third = api.v1ClausesPost(dsl);

            Assertions.assertTrue(first.stream().map(Clause::getVersion).allMatch(version -> version == 1));
            Assertions.assertTrue(second.stream().map(Clause::getVersion).allMatch(version -> version == 2));
            Assertions.assertTrue(third.stream().map(Clause::getVersion).allMatch(version -> version == 3));

        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

}
