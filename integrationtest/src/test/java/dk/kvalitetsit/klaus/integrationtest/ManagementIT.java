package dk.kvalitetsit.klaus.integrationtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.ManagementApi;
import org.openapitools.client.model.Clause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClientResponseException;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static dk.kvalitetsit.klaus.integrationtest.MockFactory.clauseDto;


public class ManagementIT extends BaseTest {

    private ManagementApi api;

    @BeforeEach
    void setup(@Autowired @Qualifier("validationDataSource") DataSource dataSource) {
        this.api = new ManagementApi(client);
        resetDB(dataSource);
    }

    private static void resetDB(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SHOW FULL TABLES WHERE Table_type = 'BASE TABLE'");

        for (Map<String, Object> row : rows) {
            String tableName = (String) row.values().toArray()[0];
            if (!tableName.endsWith("_seq")) {
                jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
            }
        }

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
    }

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

            Assertions.assertEquals(created.getFirst(), deleted);
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

            Assertions.assertTrue(first.stream().map(Clause::getVersion).allMatch(version -> version != null && version == 1));
            Assertions.assertTrue(second.stream().map(Clause::getVersion).allMatch(version -> version != null && version == 2));
            Assertions.assertTrue(third.stream().map(Clause::getVersion).allMatch(version -> version != null && version == 3));

        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testReadClauseSet() {
        final List<Clause> dsl = List.of(clauseDto);
        try {
            var created = api.v1ClausesPost(dsl);
            var read = api.v1ClausesGet(null, null); // <- Setting the pagination to null should result reading everything
            Assertions.assertEquals(created.getFirst().getName(), read.getFirst().getName());
            Assertions.assertEquals(created.getFirst().getExpression(), read.getFirst().getExpression());
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

}
