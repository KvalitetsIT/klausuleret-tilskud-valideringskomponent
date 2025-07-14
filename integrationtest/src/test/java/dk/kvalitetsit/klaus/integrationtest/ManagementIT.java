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

        final List<String> set = List.of(dsl);

        try {
            var response = api.v1ClausesDslPost(set);
            Assertions.assertEquals(set, response);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPostClauseSet() {
        final List<Clause> dsl = List.of(clauseDto);
        try {
            var response = api.v1ClausesPost(dsl);
            Assertions.assertEquals(dsl, response);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

}
