package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Field;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.integrationtest.MockFactory;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.ExpressionRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseQuery;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

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
    void createAndReadDraftWithParent() {
        var clauseInputWithoutParent = new ClauseEntityInput("clause1", MockFactory.EXPRESSION_1_ENTITY, "message1", Clause.Status.DRAFT, "tester1", null, null);
        var createdClauseWithoutParent = repository.create(clauseInputWithoutParent);
        var clauseInput = new ClauseEntityInput("clause2", MockFactory.EXPRESSION_1_ENTITY, "message2", Clause.Status.DRAFT, "tester2", createdClauseWithoutParent.id(), null);

        var createdClause = repository.create(clauseInput);
        var readClause = repository.read(createdClause.uuid());

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        assertEquals(createdClause, readClause.get(), "The clause read is expected to match the clause created");
        assertNotNull(createdClause.id(), "An id is expected to be assigned by the database when writing a clause");
        assertNotNull(createdClause.uuid(), "A uuid is expected to be assigned by the database when writing a clause");
        assertNotNull(createdClause.createdTime(), "createdTime is expected to be set by the database when writing a clause");

        var pathsToIgnore = Stream.concat(ignoreFieldRecursive(createdClause.expression(), "id", "expression."),
                        Stream.of("id", "uuid", "errorCode", "createdTime"))
                .toArray(String[]::new);
        assertThat(createdClause)
                .usingRecursiveComparison()
                .ignoringFields(pathsToIgnore)
                .withFailMessage("Expected the clause returned to be equal to the one given as argument")
                .isEqualTo(clauseInput);
    }

    @Test
    void createAndReadInactiveClause() {
        var clauseInput = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", Clause.Status.INACTIVE, "tester", null, null);

        var createdClause = repository.create(clauseInput);
        var readClause = repository.read(createdClause.uuid());

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        assertEquals(createdClause, readClause.get(), "The clause read is expected to match the clause created");
        assertNotNull(createdClause.id(), "An id is expected to be assigned by the database when writing a clause");
        assertNotNull(createdClause.uuid(), "A uuid is expected to be assigned by the database when writing a clause");

        var pathsToIgnore = Stream.concat(ignoreFieldRecursive(createdClause.expression(), "id", "expression."),
                        Stream.of("id", "uuid", "errorCode", "createdTime"))
                .toArray(String[]::new);
        assertThat(createdClause)
                .usingRecursiveComparison()
                .ignoringFields(pathsToIgnore)
                .withFailMessage("Expected the clause returned to be equal to the one given as argument")
                .isEqualTo(clauseInput);
    }

    @Test
    void createAndReadDraftsWithoutChildren() {
        var clauseInput1 = new ClauseEntityInput("clause1", MockFactory.EXPRESSION_1_ENTITY, "message1", Clause.Status.DRAFT, "tester", null, null);
        var clauseInput2 = new ClauseEntityInput("clause2", MockFactory.EXPRESSION_1_ENTITY, "message2", Clause.Status.DRAFT, "tester", null, null);

        var createdClause1 = repository.create(clauseInput1);
        var createdClause2 = repository.create(clauseInput2);
        var readClauses = repository.read(new ClauseQuery().statuses(Clause.Status.DRAFT).withoutChildren());

        assertEquals(2, readClauses.size());
        assertTrue(readClauses.contains(createdClause1));
        assertTrue(readClauses.contains(createdClause2));
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

    @Test
    void read_WithNoClauses_ReturnsEmptyList() {
        var clauses = repository.read(new ClauseQuery());

        assertTrue(clauses.isEmpty(), "Expected no clauses to be returned when no clauses exist");
    }

    @Test
    void read_WithAllTypesOfClausesAndNoQueryParameters_ReturnsAllClauses() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("clause1", expression, "error", Clause.Status.DRAFT, "tester", null, null);
        var clause1 = repository.create(clauseInput1);
        var clauseInput2 = new ClauseEntityInput("clause2", expression, "error", Clause.Status.DRAFT, "tester", clause1.id(), null);
        var clause2 = repository.create(clauseInput2);
        var clauseInput3 = new ClauseEntityInput("clause3", expression, "error", Clause.Status.ACTIVE, "tester", null, clause2.id());
        var clause3 = repository.create(clauseInput3);
        var clauseInput4 = new ClauseEntityInput("clause4", expression, "error", Clause.Status.INACTIVE, "tester", clause3.id(), null);
        var clause4 = repository.create(clauseInput4);

        var clauses = repository.read(new ClauseQuery());

        assertEquals(4, clauses.size(), "Expected all clauses to be returned");
        assertTrue(clauses.contains(clause1));
        assertTrue(clauses.contains(clause2));
        assertTrue(clauses.contains(clause3));
        assertTrue(clauses.contains(clause4));
    }

    @Test
    void read_WithNameInQuery_ReturnsClauseWithMatchingName() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("clause1", expression, "error", Clause.Status.DRAFT, "tester", null, null);
        var clause1 = repository.create(clauseInput1);
        var clauseInput2 = new ClauseEntityInput("clause2", expression, "error", Clause.Status.DRAFT, "tester", clause1.id(), null);
        var clause2 = repository.create(clauseInput2);
        var clauseInput3 = new ClauseEntityInput("clause3", expression, "error", Clause.Status.ACTIVE, "tester", null, clause2.id());
        var clause3 = repository.create(clauseInput3);
        var clauseInput4 = new ClauseEntityInput("clause1", expression, "error", Clause.Status.INACTIVE, "tester", clause3.id(), null);
        var clause4 = repository.create(clauseInput4);

        var clauses = repository.read(new ClauseQuery().name("clause1"));

        assertEquals(2, clauses.size(), "Expected all clauses to be returned");
        assertTrue(clauses.contains(clause1));
        assertTrue(clauses.contains(clause4));
    }

    @Test
    void readDraftsWithoutChildren_ReturnsDraftsWithNoChildClauses() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("clause1", expression, "errorA", Clause.Status.DRAFT, "tester", null, null);
        var clause1 = repository.create(clauseInput1);
        var clauseInput2 = new ClauseEntityInput("clause2", expression, "errorB", Clause.Status.DRAFT, "tester", null, null);
        var clause2 = repository.create(clauseInput2);
        var clauseInput3 = new ClauseEntityInput("clause3", expression, "errorB", Clause.Status.DRAFT, "tester", null, null);
        var clause3 = repository.create(clauseInput3);
        var clauseInput4 = new ClauseEntityInput("clause4", expression, "errorB", Clause.Status.DRAFT, "tester", clause2.id(), null);
        var clause4 = repository.create(clauseInput4);
        var clauseInput5 = new ClauseEntityInput("clause5", expression, "errorB", Clause.Status.ACTIVE, "tester", null, clause3.id());
        repository.create(clauseInput5);

        var clauses = repository.read(new ClauseQuery().statuses(Clause.Status.DRAFT).withoutChildren());

        assertEquals(2, clauses.size(), "Expected current clauses to be returned");
        assertTrue(clauses.contains(clause1));
        assertTrue(clauses.contains(clause4));
    }

    @Test
    void readActiveAndInactiveClauses_WithNoActiveOrInactiveClauses_ReturnsEmptyList() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput = new ClauseEntityInput("blaah", expression, "errorA", Clause.Status.DRAFT, "tester", null, null);
        repository.create(clauseInput);

        var clauses = repository.read(new ClauseQuery().statuses(Clause.Status.ACTIVE, Clause.Status.INACTIVE));

        assertTrue(clauses.isEmpty(), "Expected no clauses to be returned when no matching clauses exist");
    }

    @Test
    void readActiveClausesWithoutPrimaryChildren_ReturnsActiveClauseWithoutPrimaryChild() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("blaah", expression, "errorA", Clause.Status.ACTIVE, "tester", null, null);
        var clauseA = repository.create(clauseInput1);
        var clauseInput2 = new ClauseEntityInput("blaah", expression, "errorB", Clause.Status.ACTIVE, "tester", clauseA.id(), null);
        var clauseB = repository.create(clauseInput2);

        var clauses = repository.read(new ClauseQuery().statuses(Clause.Status.ACTIVE).withoutPrimaryChildren());

        assertEquals(1, clauses.size(), "Expected only the latest active version of the clause");
        assertThat(clauses.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(clauseB);
    }

    @Test
    void readActiveClausesWithNameWithoutPrimaryChildren_WithActiveClausesWithDraftChild_ReturnsLatestActiveClause() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("blaah", expression, "errorA", Clause.Status.ACTIVE, "tester", null, null);
        var clause1 = repository.create(clauseInput1);
        var clauseInput2 = new ClauseEntityInput("blaah", expression, "errorB", Clause.Status.ACTIVE, "tester", clause1.id(), null);
        var clause2 = repository.create(clauseInput2);
        var draftClauseInput = new ClauseEntityInput("blaah", expression, "errorB", Clause.Status.DRAFT, "tester", null, clause2.id());
        repository.create(draftClauseInput);

        var clauses = repository.read(new ClauseQuery().statuses(Clause.Status.ACTIVE).withoutPrimaryChildren());

        assertEquals(1, clauses.size(), "Expected only the latest active version of the clause");
        assertThat(clauses.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(clause2);
    }

    @Test
    void createActiveWithInactiveClauseChild_ThenReadNonDraftClausesWithoutPrimaryChildren_ReturnsTheLatestClause() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("blaah", expression, "errorA", Clause.Status.ACTIVE, "tester", null, null);
        var clause1 = repository.create(clauseInput1);
        var clauseInput2 = new ClauseEntityInput("blaah", expression, "errorB", Clause.Status.INACTIVE, "tester", clause1.id(), null);
        var clause2 = repository.create(clauseInput2);

        var clause = repository.read(new ClauseQuery().name("blaah").statuses(Clause.Status.ACTIVE, Clause.Status.INACTIVE).withoutPrimaryChildren());

        assertEquals(1, clause.size());
        assertThat(clause.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(clause2);
    }

    @Test
    void createDraftWithChildClauses_ThenReadDraftWithoutChildren_ReturnsTheDraftClauseWithNoChildren() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("blaah", expression, "errorA", Clause.Status.DRAFT, "tester", null, null);
        var clause1 = repository.create(clauseInput1);
        var clauseInput2 = new ClauseEntityInput("blaah", expression, "errorB", Clause.Status.ACTIVE, "tester", null, clause1.id());
        var clause2 = repository.create(clauseInput2);
        var clauseInput3 = new ClauseEntityInput("blaah", expression, "errorC", Clause.Status.DRAFT, "tester", null, clause2.id());
        var clause3 = repository.create(clauseInput3);
        var clauseInput4 = new ClauseEntityInput("blaah", expression, "errorD", Clause.Status.DRAFT, "tester", clause3.id(), null);
        var clause4 = repository.create(clauseInput4);

        var clauses = repository.read(new ClauseQuery().name("blaah").statuses(Clause.Status.DRAFT).withoutChildren());

        assertEquals(1, clauses.size());
        assertThat(clauses.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(clause4);
    }

    @Test
    void createDraftClauseWithAnActiveChild_ThenReadDraftWithoutChildren_ReturnsNothing() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput1 = new ClauseEntityInput("blaah", expression, "errorA", Clause.Status.DRAFT, "tester", null, null);
        var clause1 = repository.create(clauseInput1);
        var clauseInput2 = new ClauseEntityInput("blaah", expression, "errorB", Clause.Status.ACTIVE, "tester", null, clause1.id());
        repository.create(clauseInput2);

        var latestClauses = repository.read(new ClauseQuery().name("blaah").statuses(Clause.Status.DRAFT).withoutChildren());

        assertTrue(latestClauses.isEmpty());
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
        var clauseInput = new ClauseEntityInput("ClauseName", expression, "message", Clause.Status.DRAFT, "tester", null, null);

        var created = repository.create(clauseInput);

        var pathsToIgnore = Stream.concat(ignoreFieldRecursive(created.expression(), "id", "expression."),
                        Stream.of("id", "uuid", "errorCode", "createdTime"))
                .toArray(String[]::new);
        assertThat(created)
                .usingRecursiveComparison()
                .ignoringFields(pathsToIgnore)
                .withFailMessage("Expected the clause returned to be equal to the one given as argument")
                .isEqualTo(clauseInput);

        var read = repository.read(created.uuid());

        assertTrue(read.isPresent(), "Expected to read the clause previously created");
        assertEquals(created, read.get(), "Expected the same clause as previously created");
    }

    @Test
    void testCreateAndReadExistingDrugMedicationCondition() {
        var existingDrugMedicationCondition = new ExpressionEntity.ExistingDrugMedicationConditionEntity(null, "ATC", "form", "adm");
        var clauseInput = new ClauseEntityInput("CLAUSE", existingDrugMedicationCondition, "message", Clause.Status.DRAFT, "tester", null, null);

        UUID clauseUuid = repository.create(clauseInput).uuid();
        var readClause = repository.read(clauseUuid);

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        var expectedClause = new ClauseEntity(null, null, "CLAUSE", Clause.Status.DRAFT, 10800, "message", existingDrugMedicationCondition, "tester", null, null, null);
        assertThat(readClause.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "uuid", "errorCode", "expression.id", "createdTime")
                .withFailMessage("The clause read is expected to match the clause created")
                .isEqualTo(expectedClause);
    }

    @Test
    void readHistory_WithBothActiveInactiveAndDrafts_ReturnsOnlyActiveAndInactive() {
        var ageCondition = new ExpressionEntity.NumberConditionEntity(null, Field.AGE, Operator.EQUAL, 10);
        String clauseName = "test";

        var activeClauseInput = new ClauseEntityInput(clauseName, ageCondition, "message", Clause.Status.ACTIVE, "tester", null, null);
        var inactiveClauseInput = new ClauseEntityInput(clauseName, ageCondition, "message", Clause.Status.INACTIVE, "tester", null, null);
        var draftClauseInput = new ClauseEntityInput(clauseName, ageCondition, "message", Clause.Status.DRAFT, "tester", null, null);
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
        var clauseInput = new ClauseEntityInput("test", ageCondition, "test", Clause.Status.DRAFT, "tester", null, null);
        repository.create(clauseInput);

        var history = repository.readHistory(clauseInput.name());

        assertTrue(history.isEmpty(), "Expected no clauses to be returned since only a draft version of the clause exists");
    }

    @Test
    void create_WhenAllErrorCodesHasBeenUsed_ThrowsException() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(appDatabase.getDatasource());
        jdbcTemplate.execute("INSERT INTO error_code (error_code, clause_name) VALUES (10999, 'clause_with_last_error_code')");
        var clauseInput = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", Clause.Status.DRAFT, "tester", null, null);

        var e = assertThrows(RuntimeException.class, () -> repository.create(clauseInput));

        Assertions.assertEquals("Failed to create clause", e.getMessage());
    }

    @Test
    void create_WhenDbContainsErrorCodeBelowAllowedRange_ThrowsException() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(appDatabase.getDatasource());
        jdbcTemplate.execute("INSERT INTO error_code (error_code, clause_name) VALUES (10799, 'clause_with_invalid_error_code')");
        var clause = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", Clause.Status.DRAFT, "tester", null, null);

        var e = assertThrows(RuntimeException.class, () -> repository.create(clause));

        Assertions.assertEquals("Failed to create clause", e.getMessage());
    }

    @Test
    void deleteDraft_givenStatusNotDraft_ThrowsException() {
        for (var status : Clause.Status.values()) {
            if (status != Clause.Status.DRAFT) {
                var clause = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", status, "tester", null, null);
                var created = repository.create(clause);
                var e = assertThrows(NotFoundException.class, () -> repository.deleteDraft(created.uuid()));
                Assertions.assertEquals("No clause found with uuid %s and status DRAFT".formatted(created.uuid()), e.getMessage());
            }
        }
    }

    @Test
    void deleteDraft_givenStatusDraft_SuccessfullyDeletesClauseAndExpressions() {
        var jdbcTemplate = new JdbcTemplate(appDatabase.getDatasource());
        var clause = new ClauseEntityInput("clause", MockFactory.EXPRESSION_1_ENTITY, "message", Clause.Status.DRAFT, "tester", null, null);
        var created = repository.create(clause);

        assertDoesNotThrow(() -> repository.deleteDraft(created.uuid()));

        var clauseAfterDeletion = repository.read(created.uuid());
        assertTrue(clauseAfterDeletion.isEmpty(), "Did not expect clause in db after deletion");
        Integer expressionCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM expression", Integer.class);
        assertEquals(0, expressionCount, "Expected clause expression and child expressions to be deleted when deleting the clause");
    }

    @Test
    void readParent_WhenNoClauseMatchesUuid_ReturnsEmpty() {
        var parent = repository.readParent(UUID.randomUUID());

        assertTrue(parent.isEmpty(), "Expected no parent clause for a non-existing clause");
    }

    @Test
    void readParent_WhenClauseHasNoParent_ReturnsEmpty() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var clauseInput = new ClauseEntityInput("clause", expression, "message", Clause.Status.DRAFT, "tester", null, null);
        var createdClause = repository.create(clauseInput);

        var parent = repository.readParent(createdClause.uuid());

        assertTrue(parent.isEmpty(), "Expected no parent clause for a clause with no parent");
    }

    @Test
    void readParent_WhenClauseHasParent_ReturnsParent() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");
        var parentInput = new ClauseEntityInput("clause1", expression, "message1", Clause.Status.DRAFT, "tester", null, null);
        var createdParent = repository.create(parentInput);
        var childInput = new ClauseEntityInput("clause2", expression, "message2", Clause.Status.DRAFT, "tester", createdParent.id(), null);
        var createdChild = repository.create(childInput);

        var parent = repository.readParent(createdChild.uuid());

        assertTrue(parent.isPresent(), "Expected to find a parent clause for the child clause");
        assertEquals(createdParent, parent.get(), "Expected the parent clause to match the one created first");
    }
}
