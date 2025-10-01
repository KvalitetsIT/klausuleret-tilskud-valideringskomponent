package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ClauseRepositoryImplIT extends BaseTest {

    private ClauseRepositoryImpl repository;

    @BeforeAll
    void setup() {
        this.repository = new ClauseRepositoryImpl(appDatabase.getDatasource());
    }

    @Test
    void testReadAll() {
        var clause1 = new ClauseForCreation("clause1", MockFactory.EXPRESSION_1_ENTITY);
        var clause2 = new ClauseForCreation("clause2", MockFactory.EXPRESSION_1_ENTITY);

        var clauses = List.of(clause1, clause2);

        var written = clauses.stream().map(repository::create).toList();
        var read = this.repository.readAll();
        assertEquals(clauses.size(), read.size());
        for (int i = 0; i < written.size(); i++) {

            var clauseForCreation = clauses.get(i);

            var writtenClause = written.get(i);
            var readClause = read.get(i);

            assertNotNull(writtenClause.id(), "An id is expected to be assigned by the database when writing a clause");
            assertNotNull(writtenClause.uuid(), "A uuid is expected to be assigned by the database when writing a clause");
            assertNotNull(writtenClause.errorCode(), "An error code is expected to be assigned by the database when writing a clause");
            assertEquals(clauseForCreation.name(), writtenClause.name(), "The input name is expected to be used in the written clause");
            assertThat(writtenClause.expression())
                    .usingRecursiveComparison()
                    .ignoringFields("id", "left.id", "right.id", "right.left.id", "right.right.id", "right.left.left.id", "right.left.right.id")
                    .withFailMessage("The input expression is expected to be used in the written clause")
                    .isEqualTo(clauseForCreation.expression());
            assertEquals(writtenClause, readClause, "The clause read from the database is expected to match the one written beforehand");
        }
    }

    @Override
    protected void load(ClauseRepository repository) {
        // Load data before component initialization
    }

    @Test
    void assertExceptionWhen199IsExceeded() {
        Assertions.assertThrowsExactly(
                ServiceException.class,
                () -> IntStream.rangeClosed(10800, 11000).parallel().mapToObj((i) -> new ClauseForCreation("clause" + i, MockFactory.EXPRESSION_1_ENTITY)).forEach(repository::create),
                "An error is expected since only 199 clauses should be creatable as the limit of error code would be exceeded otherwise"
        );
    }

    @Test
    void assertSuccessfulCreationOf199Clauses() {
        final int OFFSET = 10800;
        final int LIMIT = 200;

        var written = IntStream.range(OFFSET, OFFSET + LIMIT).parallel().mapToObj((i) -> new ClauseForCreation("clause" + i, MockFactory.EXPRESSION_1_ENTITY)).map(repository::create).toList();

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
        var clauseForCreation = new ClauseForCreation("CLAUSE", existingDrugMedicationCondition);

        UUID clauseUuid = repository.create(clauseForCreation).uuid();
        var readClause = repository.read(clauseUuid);

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        var expectedClause = new ClauseEntity(null, null, "CLAUSE", null, existingDrugMedicationCondition);
        assertThat(readClause.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "uuid", "errorCode", "expression.id")
                .withFailMessage("The clause read is expected to match the clause created")
                .isEqualTo(expectedClause);
    }

    @Test
    void getClauseIdsByErrorCodes_WithoutErrorCodes_ReturnsEmptyList() {
        var condition = new ExpressionEntity.StringConditionEntity(Expression.Condition.Field.INDICATION, "test");
        repository.create(new ClauseForCreation("clause1", condition));

        var result = repository.getClauseIdsByErrorCodes(List.of());

        assertTrue(result.isEmpty(), "An empty list is expected when no error codes are provided");
    }

    @Test
    void getClauseIdsByErrorCodes_WithMultipleErrorCodes_ReturnsClauseIdsOfClausesWithErrorCodes() {
        var condition = new ExpressionEntity.StringConditionEntity(Expression.Condition.Field.INDICATION, "test");
        var clause1 = repository.create(new ClauseForCreation("clause1", condition));
        var clause2 = repository.create(new ClauseForCreation("clause2", condition));
        repository.create(new ClauseForCreation("clause3", condition));

        var result = repository.getClauseIdsByErrorCodes(List.of(clause1.errorCode(), clause2.errorCode(), 0));

        assertEquals(2, result.size(), "The number of ids is expected to match the number of clauses with the provided error codes");
        assertTrue(result.contains(clause1.id()), "The returned ids should contain id of clause matching input error code");
        assertTrue(result.contains(clause2.id()), "The returned ids should contain id of clause matching input error code");
    }
}
