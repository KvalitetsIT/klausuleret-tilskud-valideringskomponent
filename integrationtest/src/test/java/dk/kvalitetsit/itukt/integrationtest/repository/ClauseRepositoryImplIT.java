package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Field;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.ExpressionRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ClauseRepositoryImplIT extends BaseTest {

    private ClauseRepositoryImpl repository;

    @BeforeAll
    void setup() {
        this.repository = new ClauseRepositoryImpl(appDatabase.getDatasource(), new ExpressionRepositoryImpl(appDatabase.getDatasource()));
    }

    @Test
    void testReadAllDrafts() {
        var clause1 = new ClauseInput("clause1", MockFactory.EXPRESSION_1_ENTITY, "message");
        var clause2 = new ClauseInput("clause2", MockFactory.EXPRESSION_1_ENTITY, "message");

        var clauses = List.of(clause1, clause2);

        var written = clauses.stream().map(repository::create).toList();
        var read = this.repository.readAllDrafts();
        assertEquals(clauses.size(), read.size());
        for (int i = 0; i < written.size(); i++) {

            var clauseForCreation = clauses.get(i);

            var writtenClause = written.get(i);
            var readClause = read.get(i);

            assertNotNull(writtenClause.id(), "An id is expected to be assigned by the database when writing a clause");
            assertNotNull(writtenClause.uuid(), "A uuid is expected to be assigned by the database when writing a clause");
            assertNotNull(writtenClause.errorCode(), "An error code is expected to be assigned by the database when writing a clause");
            assertEquals(clauseForCreation.name(), writtenClause.name(), "The input name is expected to be used in the written clause");
            var idPathsToIgnore = ignoreFieldRecursive(writtenClause.expression(), "id");
            assertThat(writtenClause.expression())
                    .usingRecursiveComparison()
                    .ignoringFields(idPathsToIgnore.toArray(new String[0]))
                    .withFailMessage("The input expression is expected to be used in the written clause")
                    .isEqualTo(clauseForCreation.expression());
            assertEquals(writtenClause, readClause, "The clause read from the database is expected to match the one written beforehand");
        }
    }

    List<String> ignoreFieldRecursive(ExpressionEntity expression, String fieldToIgnore) {
        return ignoreFieldRecursive(expression, fieldToIgnore, "").toList();
    }

    Stream<String> ignoreFieldRecursive(ExpressionEntity expression, String fieldToIgnore, String path) {
        return switch (expression) {
            case ExpressionEntity.BinaryExpressionEntity binaryExp -> Stream.concat(
                    Stream.of(path + "id"),
                    Stream.concat(
                            ignoreFieldRecursive(binaryExp.left(), fieldToIgnore, path + "left."),
                            ignoreFieldRecursive(binaryExp.right(), fieldToIgnore, path + "right.")));
            case ExpressionEntity.ExistingDrugMedicationConditionEntity ignored -> Stream.of(path + fieldToIgnore);
            case ExpressionEntity.NumberConditionEntity ignored -> Stream.of(path + fieldToIgnore);
            case ExpressionEntity.StringConditionEntity ignored -> Stream.of(path + fieldToIgnore);
        };
    }

    @Override
    protected void load(ClauseRepository repository) {
        // Load data before component initialization
    }

    @Test
    void assertExceptionWhen199IsExceeded() {
        var e = Assertions.assertThrows(
                ServiceException.class,
                () -> IntStream.rangeClosed(10800, 11000)
                        .parallel()
                        .mapToObj((i) -> new ClauseInput("clause" + i, MockFactory.EXPRESSION_1_ENTITY, "message"))
                        .forEach(repository::create),
                "An error is expected since only 199 clauses should be creatable as the limit of error code would be exceeded otherwise"
        );

        assertEquals("Failed to create clause", e.getMessage());
    }

    @Test
    void assertSuccessfulCreationOf199Clauses() {
        final int OFFSET = 10800;
        final int LIMIT = 200;

        var written = IntStream.range(OFFSET, OFFSET + LIMIT)
                .parallel()
                .mapToObj((i) -> new ClauseInput("clause" + i, MockFactory.EXPRESSION_1_ENTITY, "message"))
                .map(repository::create).toList();

        Assertions.assertEquals(LIMIT, written.size(), LIMIT + " written clauses is expected since FMK only allocates error codes from " + LIMIT + " - " + (OFFSET + LIMIT - 1));

        var read = this.repository.readAllDrafts();
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
                .mapToObj(i -> new ClauseInput("clause" + i, MockFactory.EXPRESSION_1_ENTITY, "blah"))
                .forEach(repository::create));

        var err = Assertions.assertThrows(
                ServiceException.class,
                () -> repository.create(new ClauseInput("clause" + 200, MockFactory.EXPRESSION_1_ENTITY, "blah"))
        );

        Assertions.assertEquals("Failed to create clause", err.getMessage());
    }

    @Test
    void createTwoClauseWithSameName_ThenReadAllDrafts_ReturnsBothClauses() {
        var clauseAInput = new ClauseInput("blaah", new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah"), "errorA");
        var clauseBInput = new ClauseInput("blaah", new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah"), "errorB");

        var clauseA = repository.create(clauseAInput);
        var clauseB = repository.create(clauseBInput);
        var clauses = repository.readAllDrafts();

        assertEquals(2, clauses.size(), "Expected both clauses to be returned");
        assertTrue(clauses.contains(clauseA));
        assertTrue(clauses.contains(clauseB));
    }

    @Test
    void createAndApproveTwoClauseWithSameName_ThenReadAllActive_ReturnsLatestApprovedClause() {
        var clauseAInput = new ClauseInput("blaah", new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah"), "errorA");
        var clauseBInput = new ClauseInput("blaah", new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah"), "errorB");

        var clauseA = repository.create(clauseAInput);
        var clauseB = repository.create(clauseBInput);
        repository.updateDraftToActive(clauseB.uuid());
        repository.updateDraftToActive(clauseA.uuid());
        var clauses = repository.readAllActive();

        assertEquals(1, clauses.size(), "Expected only the latest approved version of the clause");
        assertEquals(clauseA, clauses.getFirst(),
                "Expected the latest approved version of the clause to be returned");
    }

    @Test
    void givenADeepClause_whenCreateAndRead_thenAssertEqual() {

        var deepClause = new ClauseInput("ClauseName", new ExpressionEntity.BinaryExpressionEntity(
                new ExpressionEntity.BinaryExpressionEntity(
                        new ExpressionEntity.BinaryExpressionEntity(
                                new ExpressionEntity.StringConditionEntity(Field.AGE, "whatEver"),
                                BinaryExpression.Operator.OR,
                                new ExpressionEntity.NumberConditionEntity(Field.INDICATION, Operator.EQUAL, 20)
                        ),
                        BinaryExpression.Operator.OR,
                        new ExpressionEntity.ExistingDrugMedicationConditionEntity(1L, "atcCode", "formCode", "routeOfAdministration")
                ),
                BinaryExpression.Operator.AND,
                new ExpressionEntity.BinaryExpressionEntity(
                        new ExpressionEntity.StringConditionEntity(Field.INDICATION, "whatEver"),
                        BinaryExpression.Operator.AND,
                        new ExpressionEntity.NumberConditionEntity(Field.AGE, Operator.GREATER_THAN, 20)
                )
        ), "message");

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
        var clauseForCreation = new ClauseInput("CLAUSE", existingDrugMedicationCondition, "message");

        UUID clauseUuid = repository.create(clauseForCreation).uuid();
        var readClause = repository.read(clauseUuid);

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        var expectedClause = new ClauseEntity(null, null, "CLAUSE", 10800, "message", existingDrugMedicationCondition, readClause.get().createdAt());
        assertThat(readClause.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "uuid", "errorCode", "expression.id")
                .withFailMessage("The clause read is expected to match the clause created")
                .isEqualTo(expectedClause);
    }

    @Test
    void readHistory_assertVersionAreReturnedAfterUpdates() {
        var ageCondition = new ExpressionEntity.NumberConditionEntity(null, Field.AGE, Operator.EQUAL, 10);

        int numberOfCreates = 10;

        for (int i = 0; i < numberOfCreates; i++)
            repository.create(new ClauseInput("UPDATED_CLAUSE", ageCondition, "message-" + 1));

        var versions = repository.readHistory("UPDATED_CLAUSE");

        assertEquals(numberOfCreates, versions.size(), "Expected the same number of versions of the clause as it was updated");
    }

    @Test
    void updateDraftToActive_WhenNoClauseWithUuidExist_ThrowsException() {
        UUID nonExistingUuid = UUID.randomUUID();

        var e = assertThrows(
                NotFoundException.class,
                () -> repository.updateDraftToActive(nonExistingUuid));

        assertEquals("No clause found with uuid %s in DRAFT status".formatted(nonExistingUuid), e.getDetailedError());
    }

    @Test
    void updateDraftToActive_WhenUuidMatchesClause_SucceedsOnlyWhenInDraft() {
        var clauseInput = new ClauseInput("test", new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah"), "error");
        var clause = repository.create(clauseInput);

        assertDoesNotThrow(() -> repository.updateDraftToActive(clause.uuid()));
        var e = assertThrows(
                NotFoundException.class,
                () -> repository.updateDraftToActive(clause.uuid()));

        assertEquals("No clause found with uuid %s in DRAFT status".formatted(clause.uuid()), e.getDetailedError());
    }
}
