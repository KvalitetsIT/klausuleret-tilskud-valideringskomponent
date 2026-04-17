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
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
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
    void createAndReadDraft() {
        var clauseInput = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", Clause.Status.DRAFT, null);

        var createdClause = repository.create(clauseInput);
        var readClause = repository.read(createdClause.uuid());

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        assertEquals(createdClause, readClause.get(), "The clause read is expected to match the clause created");
        assertNotNull(createdClause.id(), "An id is expected to be assigned by the database when writing a clause");
        assertNotNull(createdClause.uuid(), "A uuid is expected to be assigned by the database when writing a clause");
        assertTrue(createdClause.validFrom().isEmpty(), "validFrom is expected to be empty when not included in the input");
        assertEquals(clauseInput.name(), createdClause.name(), "The input name is expected to be used in the written clause");
        assertEquals(clauseInput.errorMessage(), createdClause.errorMessage(), "The input error message is expected to be used in the written clause");
        assertEquals(clauseInput.status(), createdClause.status(), "The input status is expected to be used in the written clause");
        var idPathsToIgnore = ignoreFieldRecursive(createdClause.expression(), "id");
        assertThat(createdClause.expression())
                .usingRecursiveComparison()
                .ignoringFields(idPathsToIgnore)
                .withFailMessage("The input expression is expected to be used in the written clause")
                .isEqualTo(clauseInput.expression());
    }

    @Test
    void createAndReadInactiveClause() {
        var clauseInput = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", Clause.Status.INACTIVE, new Date());

        var createdClause = repository.create(clauseInput);
        var readClause = repository.read(createdClause.uuid());

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        assertEquals(createdClause, readClause.get(), "The clause read is expected to match the clause created");
        assertNotNull(createdClause.id(), "An id is expected to be assigned by the database when writing a clause");
        assertNotNull(createdClause.uuid(), "A uuid is expected to be assigned by the database when writing a clause");
        assertEquals(clauseInput.name(), createdClause.name(), "The input name is expected to be used in the written clause");
        assertEquals(clauseInput.errorMessage(), createdClause.errorMessage(), "The input error message is expected to be used in the written clause");
        assertEquals(clauseInput.status(), createdClause.status(), "The input status is expected to be used in the written clause");
        assertTrue(createdClause.validFrom().isPresent(), "validFrom is expected to be set when included in the input");
        assertEquals(clauseInput.validFrom(), createdClause.validFrom().get(), "The input validFrom is expected to be used in the written clause");
        var idPathsToIgnore = ignoreFieldRecursive(createdClause.expression(), "id");
        assertThat(createdClause.expression())
                .usingRecursiveComparison()
                .ignoringFields(idPathsToIgnore)
                .withFailMessage("The input expression is expected to be used in the written clause")
                .isEqualTo(clauseInput.expression());
    }

    @Test
    void createAndReadAllDrafts() {
        var clauseInput1 = new ClauseEntityInput("clause1", MockFactory.EXPRESSION_1_ENTITY, "message1", Clause.Status.DRAFT, null);
        var clauseInput2 = new ClauseEntityInput("clause2", MockFactory.EXPRESSION_1_ENTITY, "message2", Clause.Status.DRAFT, null);

        var createdClause1 = repository.create(clauseInput1);
        var createdClause2 = repository.create(clauseInput2);
        var readClauses = repository.readAllDrafts();

        assertEquals(2, readClauses.size());
        assertTrue(readClauses.contains(createdClause1));
        assertTrue(readClauses.contains(createdClause2));
    }

    String[] ignoreFieldRecursive(ExpressionEntity expression, String fieldToIgnore) {
        return ignoreFieldRecursive(expression, fieldToIgnore, "").toArray(String[]::new);
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
    void createTwoDraftClausesWithSameName_ThenReadAllDrafts_ReturnsBothClauses() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("clause", expression, "errorA", Clause.Status.DRAFT, null);
        var clauseInput2 = new ClauseEntityInput("clause", expression, "errorB", Clause.Status.DRAFT, null);

        var clauseA = repository.create(clauseInput1);
        var clauseB = repository.create(clauseInput2);
        var clauses = repository.readAllDrafts();

        assertEquals(2, clauses.size(), "Expected both clauses to be returned");
        assertTrue(clauses.contains(clauseA));
        assertTrue(clauses.contains(clauseB));
    }

    @Test
    void createTwoActiveClausesWithSameName_ThenReadCurrentClauses_ReturnsLatestValidClause() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("blaah", expression, "errorA", Clause.Status.ACTIVE, new Date());
        var clauseInput2 = new ClauseEntityInput("blaah", expression, "errorB", Clause.Status.ACTIVE, Date.from(Instant.now().plusSeconds(1)));

        var clauseA = repository.create(clauseInput1);
        var clauseB = repository.create(clauseInput2);
        var clauses = repository.readCurrentClauses();

        assertEquals(1, clauses.size(), "Expected only the latest approved version of the clause");
        assertThat(clauses.getFirst())
                .usingRecursiveComparison()
                .ignoringFields("validFrom", "status")
                .withFailMessage("Expected the latest valid version of the clause to be returned")
                .isEqualTo(clauseB);
        assertEquals(Clause.Status.ACTIVE, clauses.getFirst().status());
    }

    @Test
    void createTwoActiveClausesWithSameName_ThenReadCurrentClause_ReturnsLatestValidClause() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("blaah", expression, "errorA", Clause.Status.ACTIVE, new Date());
        var clauseInput2 = new ClauseEntityInput("blaah", expression, "errorB", Clause.Status.ACTIVE, Date.from(Instant.now().plusSeconds(1)));

        var clauseA = repository.create(clauseInput1);
        var clauseB = repository.create(clauseInput2);
        var clause = repository.readCurrentClause("blaah");

        assertTrue(clause.isPresent());
        assertThat(clause.get())
                .usingRecursiveComparison()
                .ignoringFields("validFrom", "status")
                .withFailMessage("Expected the latest valid version of the clause to be returned")
                .isEqualTo(clauseB);
        assertEquals(Clause.Status.ACTIVE, clause.get().status());
    }

    @Test
    void createClauseWithoutValidFrom_ThenReadCurrentClause_ReturnsNothing() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        ClauseEntityInput clauseInput = new ClauseEntityInput("blaah", expression, "error", Clause.Status.DRAFT, null);

        repository.create(clauseInput);
        var latestClause = repository.readCurrentClause("blaah");

        assertTrue(latestClause.isEmpty());
    }

    @Test
    void givenADeepClause_whenCreateAndRead_thenAssertEqual() {
        var expression = new ExpressionEntity.BinaryExpressionEntity(
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
        );
        var clauseInput = new ClauseEntityInput("ClauseName", expression, "message", Clause.Status.DRAFT, null);

        var created = repository.create(clauseInput);

        var fieldsToIgnore = ignoreFieldRecursive(expression, "id");
        assertThat(created.expression())
                .usingRecursiveComparison()
                .ignoringFields(fieldsToIgnore)
                .withFailMessage("Expected the expression returned to be equal to the one given as argument")
                .isEqualTo(expression);

        var read = repository.read(created.uuid());

        assertTrue(read.isPresent(), "Expected to read the clause previously created");
        assertEquals(created, read.get(), "Expected the same clause as previously created");
    }

    @Test
    void testCreateAndReadExistingDrugMedicationCondition() {
        var existingDrugMedicationCondition = new ExpressionEntity.ExistingDrugMedicationConditionEntity(null, "ATC", "form", "adm");
        var clauseInput = new ClauseEntityInput("CLAUSE", existingDrugMedicationCondition, "message", Clause.Status.DRAFT, null);

        UUID clauseUuid = repository.create(clauseInput).uuid();
        var readClause = repository.read(clauseUuid);

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        var expectedClause = new ClauseEntity(null, null, "CLAUSE", Clause.Status.DRAFT, 10800, "message", existingDrugMedicationCondition, readClause.get().validFrom());
        assertThat(readClause.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "uuid", "errorCode", "expression.id")
                .withFailMessage("The clause read is expected to match the clause created")
                .isEqualTo(expectedClause);
    }

    @Test
    void readHistory_WithBothActiveInactiveAndDrafts_ReturnsOnlyActiveAndInactive() {
        var ageCondition = new ExpressionEntity.NumberConditionEntity(null, Field.AGE, Operator.EQUAL, 10);
        String clauseName = "test";

        var activeClauseInput = new ClauseEntityInput(clauseName, ageCondition, "message", Clause.Status.ACTIVE, Date.from(Instant.now()));
        var inactiveClauseInput = new ClauseEntityInput(clauseName, ageCondition, "message", Clause.Status.INACTIVE, Date.from(Instant.now().plusSeconds(1)));
        var draftClauseInput = new ClauseEntityInput(clauseName, ageCondition, "message", Clause.Status.DRAFT, null);
        var activeClause = repository.create(activeClauseInput);
        var inactiveClause = repository.create(inactiveClauseInput);
        var draftClause = repository.create(draftClauseInput);

        var history = repository.readHistory(clauseName);

        assertEquals(2, history.size(), "Expected only the active and inactive versions of the clause to be returned");
        assertTrue(history.contains(activeClause));
        assertTrue(history.contains(inactiveClause));
    }

    @Test
    void readHistory_WhenOnlyDraftClauseExists_ReturnsEmptyList() {
        var ageCondition = new ExpressionEntity.NumberConditionEntity(null, Field.AGE, Operator.EQUAL, 10);
        var clauseInput = new ClauseEntityInput("test", ageCondition, "test", Clause.Status.DRAFT, null);
        repository.create(clauseInput);

        var history = repository.readHistory(clauseInput.name());

        assertTrue(history.isEmpty(), "Expected no clauses to be returned since only a draft version of the clause exists");
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
        var clauseInput = new ClauseEntityInput("test", new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah"), "error", Clause.Status.DRAFT, null);

        var clause = repository.create(clauseInput);

        assertDoesNotThrow(() -> repository.updateDraftToActive(clause.uuid()));
        var e = assertThrows(
                NotFoundException.class,
                () -> repository.updateDraftToActive(clause.uuid()));

        assertEquals("No clause found with uuid %s in DRAFT status".formatted(clause.uuid()), e.getDetailedError());
    }

    @Test
    void create_WhenAllErrorCodesHasBeenUsed_ThrowsException() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(appDatabase.getDatasource());
        jdbcTemplate.execute("INSERT INTO error_code (error_code, clause_name) VALUES (10999, 'clause_with_last_error_code')");
        var clauseInput = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", Clause.Status.DRAFT, null);

        var e = assertThrows(ServiceException.class, () -> repository.create(clauseInput));

        Assertions.assertEquals("Failed to create clause", e.getMessage());
    }

    @Test
    void create_WhenDbContainsErrorCodeBelowAllowedRange_ThrowsException() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(appDatabase.getDatasource());
        jdbcTemplate.execute("INSERT INTO error_code (error_code, clause_name) VALUES (10799, 'clause_with_invalid_error_code')");
        var clause = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", Clause.Status.DRAFT, null);

        var e = assertThrows(ServiceException.class, () -> repository.create(clause));

        Assertions.assertEquals("Failed to create clause", e.getMessage());
    }

    @Test
    void delete_givenStatusNotDraft_ThrowsException() {
        for (var status : Clause.Status.values()) {
            if (status != Clause.Status.DRAFT) {
                var clause = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", status, null);
                var created = repository.create(clause);
                var e = assertThrows(ServiceException.class, () -> repository.delete(created.uuid()));
                Assertions.assertEquals("Expected status=DRAFT, was %s".formatted(status.name()), e.getMessage());
            }
        }
    }

    @Test
    void delete_givenStatusDraft_SuccessfullyDeletesClause() {
        var clause = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", Clause.Status.DRAFT, null);
        var created = repository.create(clause);
        assertDoesNotThrow(() -> repository.delete(created.uuid()));
    }
}
