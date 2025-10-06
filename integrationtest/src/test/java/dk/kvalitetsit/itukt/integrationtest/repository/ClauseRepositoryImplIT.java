package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.ExpressionRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.relational.core.sql.In;

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
        this.repository = new ClauseRepositoryImpl(appDatabase.getDatasource(), new ExpressionRepositoryImpl(appDatabase.getDatasource()));
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
    void assertFailureCreationAfter199Clauses() {
        final int OFFSET = 10800;
        final int LIMIT = 200;

        Assertions.assertDoesNotThrow(() -> IntStream.range(OFFSET, OFFSET + LIMIT)
                .parallel()
                .mapToObj(i -> new ClauseForCreation("clause" + i, MockFactory.EXPRESSION_1_ENTITY))
                .forEach(repository::create));

        var err = Assertions.assertThrows(
                ServiceException.class,
                () -> repository.create(new ClauseForCreation("clause" + 200, MockFactory.EXPRESSION_1_ENTITY))
        );

        Assertions.assertEquals("Failed to create clause", err.getMessage());
    }

    @Test
    void getTwoClauseWithSameName() {
        var clauseA = new ClauseForCreation("blaah", new ExpressionEntity.StringConditionEntity(Expression.Condition.Field.INDICATION, "blah"));
        var clauseB = new ClauseForCreation("blaah", new ExpressionEntity.StringConditionEntity(Expression.Condition.Field.INDICATION, "blah"));

        repository.create(clauseA);

        Assertions.assertThrows(ServiceException.class, () -> repository.create(clauseB), "Expected an exception since duplicate entry");
    }


    @Test
    void givenADeepClause_whenCreateAndRead_thenAssertEqual() {

        var deepClause = new ClauseForCreation("ClauseName", new ExpressionEntity.BinaryExpressionEntity(
                new ExpressionEntity.BinaryExpressionEntity(
                        new ExpressionEntity.BinaryExpressionEntity(
                            new ExpressionEntity.StringConditionEntity(Expression.Condition.Field.AGE, "whatEver"),
                                BinaryExpression.Operator.OR,
                                new ExpressionEntity.NumberConditionEntity(Expression.Condition.Field.INDICATION, Operator.EQUAL, 20)
                        ),
                        BinaryExpression.Operator.OR,
                        new ExpressionEntity.ExistingDrugMedicationConditionEntity(1L, "atcCode", "formCode", "routeOfAdministration")
                ),
                BinaryExpression.Operator.AND,
                new ExpressionEntity.BinaryExpressionEntity(
                        new ExpressionEntity.StringConditionEntity(Expression.Condition.Field.INDICATION, "whatEver"),
                        BinaryExpression.Operator.AND,
                        new ExpressionEntity.NumberConditionEntity(Expression.Condition.Field.AGE, Operator.GREATER_THAN, 20)
                )
        ));

        var created = repository.create(deepClause);

        assertThat(created.expression())
                .usingRecursiveComparison()
                .ignoringFields("id", "left.id", "right.id", "right.left.id", "right.right.id", "right.left.left.id", "right.left.right.id", "left.left.id", "left.left.left.id", "left.right.id", "left.left.right.id")
                .withFailMessage("Expected the expression returned to be equal to the one given as argument")
                .isEqualTo(deepClause.expression());

        var read = repository.read(created.uuid());

        assertTrue(read.isPresent(), "Expected to read the clause previously created");
        assertEquals(created, read.get(), "Expected the same clause as previously created");
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
}
