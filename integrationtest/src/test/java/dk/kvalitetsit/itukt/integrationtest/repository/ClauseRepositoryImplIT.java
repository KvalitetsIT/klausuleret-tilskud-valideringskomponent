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
    void createAndReadDraft() {
        var clauseName = "clause";
        var expression = MockFactory.EXPRESSION_1_ENTITY;
        String errorMessage = "message";

        var createdClause = repository.createDraft(clauseName, expression, errorMessage);
        var readClause = repository.read(createdClause.uuid());

        assertTrue(readClause.isPresent(), "A clause is expected to be read since it was just created");
        assertEquals(createdClause, readClause.get(), "The clause read is expected to match the clause created");
        assertNotNull(createdClause.id(), "An id is expected to be assigned by the database when writing a clause");
        assertNotNull(createdClause.uuid(), "A uuid is expected to be assigned by the database when writing a clause");
        assertEquals(clauseName, createdClause.name(), "The input name is expected to be used in the written clause");
        assertEquals(errorMessage, createdClause.errorMessage(), "The input error message is expected to be used in the written clause");
        var idPathsToIgnore = ignoreFieldRecursive(createdClause.expression(), "id");
        assertThat(createdClause.expression())
                .usingRecursiveComparison()
                .ignoringFields(idPathsToIgnore)
                .withFailMessage("The input expression is expected to be used in the written clause")
                .isEqualTo(expression);
    }

    @Test
    void createAndReadAllDrafts() {
        var createdClause1 = repository.createDraft("clause1", MockFactory.EXPRESSION_1_ENTITY, "message1");
        var createdClause2 = repository.createDraft("clause2", MockFactory.EXPRESSION_1_ENTITY, "message2");
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
    void createTwoClauseWithSameName_ThenReadAllDrafts_ReturnsBothClauses() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");

        var clauseA = repository.createDraft("clause", expression, "errorA");
        var clauseB = repository.createDraft("clause", expression, "errorB");
        var clauses = repository.readAllDrafts();

        assertEquals(2, clauses.size(), "Expected both clauses to be returned");
        assertTrue(clauses.contains(clauseA));
        assertTrue(clauses.contains(clauseB));
    }

    @Test
    void createAndApproveTwoClauseWithSameName_ThenReadLatestVersions_ReturnsLatestApprovedClause() {
        var expression = new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah");

        var clauseA = repository.createDraft("blaah", expression, "errorA");
        var clauseB = repository.createDraft("blaah", expression, "errorB");
        repository.updateDraftStatus(clauseB.uuid(), Clause.Status.ACTIVE);
        repository.updateDraftStatus(clauseA.uuid(), Clause.Status.INACTIVE);
        var clauses = repository.readLatestVersions();

        assertEquals(1, clauses.size(), "Expected only the latest approved version of the clause");
        assertThat(clauses.getFirst())
                .usingRecursiveComparison()
                .ignoringFields("validFrom", "status")
                .withFailMessage("Expected the latest approved version of the clause to be returned")
                .isEqualTo(clauseA);
        assertEquals(Clause.Status.INACTIVE, clauses.getFirst().status());
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

        var created = repository.createDraft("ClauseName", expression, "message");

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

        UUID clauseUuid = repository.createDraft("CLAUSE", existingDrugMedicationCondition, "message").uuid();
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
    void readHistory_assertVersionAreReturnedAfterUpdates() {
        var ageCondition = new ExpressionEntity.NumberConditionEntity(null, Field.AGE, Operator.EQUAL, 10);

        int numberOfCreates = 10;

        for (int i = 0; i < numberOfCreates; i++)
            repository.createDraft("UPDATED_CLAUSE", ageCondition, "message-" + 1);

        var versions = repository.readHistory("UPDATED_CLAUSE");

        assertEquals(numberOfCreates, versions.size(), "Expected the same number of versions of the clause as it was updated");
    }

    @Test
    void updateDraftStatus_WhenNoClauseWithUuidExist_ThrowsException() {
        UUID nonExistingUuid = UUID.randomUUID();

        var e = assertThrows(
                NotFoundException.class,
                () -> repository.updateDraftStatus(nonExistingUuid, Clause.Status.ACTIVE));

        assertEquals("No clause found with uuid %s in DRAFT status".formatted(nonExistingUuid), e.getDetailedError());
    }

    @Test
    void updateDraftStatus_WhenUuidMatchesClause_SucceedsOnlyWhenInDraft() {
        var clause = repository.createDraft("test", new ExpressionEntity.StringConditionEntity(Field.INDICATION, "blah"), "error");

        assertDoesNotThrow(() -> repository.updateDraftStatus(clause.uuid(), Clause.Status.ACTIVE));
        var e = assertThrows(
                NotFoundException.class,
                () -> repository.updateDraftStatus(clause.uuid(), Clause.Status.ACTIVE));

        assertEquals("No clause found with uuid %s in DRAFT status".formatted(clause.uuid()), e.getDetailedError());
    }

    @Test
    void createDraft_WhenAllErrorCodesHasBeenUsed_ThrowsException() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(appDatabase.getDatasource());
        jdbcTemplate.execute("INSERT INTO error_code (error_code, clause_name) VALUES (10999, 'clause_with_last_error_code')");

        var e = assertThrows(ServiceException.class, () -> repository.createDraft("clause", MockFactory.EXPRESSION_1_ENTITY, "message"));

        Assertions.assertEquals("Failed to create clause", e.getMessage());
    }
}
