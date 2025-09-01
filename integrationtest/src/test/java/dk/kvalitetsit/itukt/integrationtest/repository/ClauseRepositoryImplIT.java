package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClauseRepositoryImplIT extends BaseTest {

    private final ClauseRepositoryImpl repository;

    ClauseRepositoryImplIT(@Autowired ClauseRepositoryImpl repository) {
        this.repository = repository;
    }

    @Test
    void testReadAll() {

        var clauses = List.of(MockFactory.CLAUSE_1_ENTITY, MockFactory.CLAUSE_1_ENTITY);

        var written = clauses.stream().map(this.repository::create).toList();
        var read = this.repository.readAll();
        assertEquals(clauses.size(), read.size());
        for (int i = 0; i < written.size(); i++) {

            ClauseEntity clause = clauses.get(i);

            ClauseEntity writtenClause = written.get(i);
            ClauseEntity readClause = read.get(i);

            Assertions.assertNotEquals(clause.uuid(), writtenClause.uuid(), "The uuid of the given clause is expected to be replaced");
            Assertions.assertNotEquals(clause.uuid(), readClause.uuid(), "The uuid of the given clause is expected to be replaced");
            assertThat(readClause)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "uuid", "expression.id", "expression.left.id", "expression.right.id", "expression.right.left.id", "expression.right.right.id")
                    .isEqualTo(clause);
            assertEquals(writtenClause, readClause, "The clause read from the database is expected to match the one written beforehand");
        }
    }

    @Test
    void assertExceptionWhen199IsExceeded() {
        Assertions.assertThrowsExactly(
                ServiceException.class,
                () -> IntStream.rangeClosed(10800, 11000).mapToObj((i) -> MockFactory.CLAUSE_1_ENTITY).forEach(this.repository::create),
                "An error is expected since only 199 clauses should be creatable as the limit of error code would be exceeded otherwise"
        );
    }

    @Test
    void assertSuccessfulCreationOf199Clauses() {
        final int OFFSET = 10800;
        final int LIMIT = 200;

        var written = IntStream.range(OFFSET, OFFSET + LIMIT).mapToObj((i) -> MockFactory.CLAUSE_1_ENTITY).map(this.repository::create).toList();

        Assertions.assertEquals(LIMIT, written.size(), LIMIT + " written clauses is expected since FMK only allocates error codes from " + LIMIT + " - " + (OFFSET + LIMIT - 1));

        var read = this.repository.readAll();
        Assertions.assertEquals(LIMIT, read.size(), LIMIT + " clauses is expected to be read since this amount was written");

        Assertions.assertEquals(written, read, "Clauses read is expected to be the same as written clauses");
    }
}
