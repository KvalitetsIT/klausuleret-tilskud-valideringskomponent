package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClauseRepositoryImplIT extends BaseTest {

    private final ClauseRepositoryImpl repository;

    ClauseRepositoryImplIT(@Autowired ClauseRepositoryImpl repository) {
        this.repository = repository;
    }

    @Test
    void testReadAll() {
        var clause1 = createClause("clause1", MockFactory.EXPRESSION_1_ENTITY);
        var clause2 = createClause("clause2", MockFactory.EXPRESSION_1_ENTITY);

        var clauses = List.of(clause1, clause2);

        var written = clauses.stream().map(clause -> repository.create(clause.name(), clause.expression())).toList();
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
                    .ignoringFields("id", "uuid", "errorCode", "expression.id", "expression.left.id", "expression.right.id", "expression.right.left.id", "expression.right.right.id", "expression.right.left.left.id", "expression.right.left.right.id")
                    .isEqualTo(clause);
            assertEquals(writtenClause, readClause, "The clause read from the database is expected to match the one written beforehand");
        }
    }

    private ClauseEntity createClause(String name, ExpressionEntity expression) {
        return new ClauseEntity(null, null, name, null, expression);
    }

    @Test
    void assertExceptionWhen199IsExceeded() {
        Assertions.assertThrowsExactly(
                ServiceException.class,
                () -> IntStream.rangeClosed(10800, 11000).parallel().mapToObj((i) -> createClause("clause" + i, MockFactory.EXPRESSION_1_ENTITY)).forEach(clause -> repository.create(clause.name(), clause.expression())),
                "An error is expected since only 199 clauses should be creatable as the limit of error code would be exceeded otherwise"
        );
    }

    @Test
    void assertSuccessfulCreationOf199Clauses() {
        final int OFFSET = 10800;
        final int LIMIT = 200;

        var written = IntStream.range(OFFSET, OFFSET + LIMIT).parallel().mapToObj((i) -> createClause("clause" + i, MockFactory.EXPRESSION_1_ENTITY)).map(clause -> repository.create(clause.name(), clause.expression())).toList();

        Assertions.assertEquals(LIMIT, written.size(), LIMIT + " written clauses is expected since FMK only allocates error codes from " + LIMIT + " - " + (OFFSET + LIMIT - 1));

        var read = this.repository.readAll();
        Assertions.assertEquals(LIMIT, read.size(), LIMIT + " clauses is expected to be read since this amount was written");

        Assertions.assertEquals(
                written.stream().sorted(Comparator.comparing(ClauseEntity::id)).toList(),
                read,
                "Clauses read is expected to be the same as written clauses"
        );
    }

    @Test
    void testCreateAndReadExistingDrugMedicationCondition() {
        var existingDrugMedicationCondition = new ExpressionEntity.ExistingDrugMedicationConditionEntity(null, "ATC", "form", "adm");

        UUID clauseUuid = repository.create("CLAUSE", existingDrugMedicationCondition).uuid();
        var readClause = repository.read(clauseUuid);

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        var expectedClause = new ClauseEntity(null, null, "CLAUSE", null, existingDrugMedicationCondition);
        assertThat(readClause.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "uuid", "errorCode", "expression.id")
                .withFailMessage("The clause read is expected to match the clause created")
                .isEqualTo(expectedClause);
    }
}
