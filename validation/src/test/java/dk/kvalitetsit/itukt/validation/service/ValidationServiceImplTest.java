package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.Cache;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static dk.kvalitetsit.itukt.common.model.ValidationError.ConditionError;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    private final Optional<ValidationFailed> someError = Optional.of(new ConditionError(ConditionError.Field.AGE, Operator.EQUAL, "20"));

    @InjectMocks
    private ValidationServiceImpl service;
    @Mock
    private ClauseService clauseService;
    @Mock
    private Cache<Long, DrugClause> drugClauseCache;
    @Mock
    private SkippedValidationService skippedValidationService;

    @Test
    void validate_WhenDrugIdDoesNotMatchClause_ReturnsSuccess() {
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy("creator"), Optional.empty(), List.of(), 5, 1234, "", Optional.empty());
        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.empty());

        var result = service.validate(validationInput);

        assertTrue(result.isEmpty());
    }

    @Test
    void validate_WhenClauseCacheDoesNotContainClauseForDrugId_ReturnsSuccess() {
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy("creator"), Optional.empty(), List.of(), 5, 1234, "", Optional.empty());
        var drugClause = new DrugClause(new DrugClause.Drug(1234L), Set.of(new DrugClause.Clause("0000", null)));
        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.of(drugClause));
        Mockito.when(clauseService.get(drugClause.clauses().iterator().next().code())).thenReturn(Optional.empty());

        var result = service.validate(validationInput);

        assertTrue(result.isEmpty());
    }

    @Test
    void validate_WhenClauseCacheContainsClauseForDrugIdAndValidationSucceeds_ReturnsSuccess() {
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy("creator"), Optional.empty(), List.of(), 5, 1234, "", Optional.empty());
        var drugClause = new DrugClause(new DrugClause.Drug(1234L), Set.of(new DrugClause.Clause("0000", null)));
        var expression = Mockito.mock(BinaryExpression.class);
        var clause = new Clause(1L, drugClause.clauses().iterator().next().code(), null, null, expression, null);

        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.of(drugClause));
        Mockito.when(clauseService.get(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(expression.validates(validationInput)).thenReturn(Optional.empty());

        var result = service.validate(validationInput);

        Mockito.verify(expression).validates(validationInput);
        assertTrue(result.isEmpty());
    }

    @Test
    void validate_WhenClauseCacheContainsClauseForDrugIdAndValidationFails_ReturnsValidationError() {
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy("creator"), Optional.empty(), List.of(), 5, 1234, "", Optional.empty());
        var drugClause = new DrugClause(new DrugClause.Drug(null), Set.of(new DrugClause.Clause("0000", "clauses text")));
        var expression = Mockito.mock(BinaryExpression.class);
        var clause = new Clause(1L, drugClause.clauses().iterator().next().code(), null, new Clause.Error("message", 10800), expression, null);
        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.of(drugClause));
        Mockito.when(clauseService.get(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(expression.validates(validationInput)).thenReturn(someError);

        var result = service.validate(validationInput);

        Mockito.verify(expression).validates(validationInput);

        assertEquals(1, result.size(), "Expected an error being returned");
        assertEquals(clause.name(), result.getFirst().clause().code());
        assertEquals(clause.error().message(), result.getFirst().clause().message());
        assertEquals(clause.error().code(), result.getFirst().code());
        assertEquals(drugClause.clauses().iterator().next().text(), result.getFirst().clause().text());
    }


    @Test
    void validate_WhenClauseCacheContainsMultipleClausesWhereTwoFails_ReturnsValidationErrors() {
        // Arrange
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy("creator"), Optional.empty(), List.of(), 5, 1234, "", Optional.empty());

        // two clauses in stamdata
        var drugClause = new DrugClause(
                new DrugClause.Drug(validationInput.drugId()),
                Set.of(
                        new DrugClause.Clause("0001", "clauses text"),
                        new DrugClause.Clause("0002", "clauses text"),
                        new DrugClause.Clause("0003", "clauses text")
                )
        );
        var clauses = drugClause.clauses().stream().toList();

        var succeedingExpression = Mockito.mock(BinaryExpression.class);
        Mockito.when(succeedingExpression.validates(validationInput)).thenReturn(Optional.empty());

        var failingExpression = Mockito.mock(BinaryExpression.class);
        Mockito.when(failingExpression.validates(validationInput)).thenReturn(someError);


        var clause_1 = new Clause(1L, clauses.get(0).code(), null, new Clause.Error(null, 10800), failingExpression, null);
        var clause_2 = new Clause(2L, clauses.get(1).code(), null, new Clause.Error(null, 10800), succeedingExpression, null);
        var clause_3 = new Clause(2L, clauses.get(2).code(), null, new Clause.Error(null, 10800), failingExpression, null);

        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.of(drugClause));

        Mockito.when(clauseService.get(clauses.get(0).code())).thenReturn(Optional.of(clause_1));
        Mockito.when(clauseService.get(clauses.get(1).code())).thenReturn(Optional.of(clause_2));
        Mockito.when(clauseService.get(clauses.get(2).code())).thenReturn(Optional.of(clause_3));

        var result = service.validate(validationInput);

        Mockito.verify(clauseService, Mockito.times(clauses.size())).get(Mockito.any());

        assertEquals(2, result.size(), "Expected the validation to fail and two validation errors to be returned");

        assertEquals(clause_1.name(), result.get(0).clause().code(), "Expected the name of the first failing clauses");
        assertEquals(clause_3.name(), result.get(1).clause().code(), "Expected the name of the second failing clauses");
    }

    @Test
    void validate_WithoutReportedBy_CreatesSkippedValidationsForCreator() {
        String creator = "creator";
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy(creator), Optional.empty(), List.of(1, 2, 3), 5, 1234, "", Optional.empty());
        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.empty());

        service.validate(validationInput);

        Mockito.verify(skippedValidationService, Mockito.times(1)).createSkippedValidations(creator, validationInput.personId(), validationInput.skippedErrorCodes());
        Mockito.verifyNoMoreInteractions(skippedValidationService);
    }

    @Test
    void validate_WithReportedBy_CreatesSkippedValidationsForCreatorAndReporter() {
        String creator = "creator";
        String reporter = "reporter";
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy(creator), Optional.of(new ValidationInput.ReportedBy(reporter)), List.of(1, 2, 3), 5, 1234, "", Optional.empty());
        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.empty());

        service.validate(validationInput);

        Mockito.verify(skippedValidationService, Mockito.times(1)).createSkippedValidations(creator, validationInput.personId(), validationInput.skippedErrorCodes());
        Mockito.verify(skippedValidationService, Mockito.times(1)).createSkippedValidations(reporter, validationInput.personId(), validationInput.skippedErrorCodes());
        Mockito.verifyNoMoreInteractions(skippedValidationService);
    }

    @Test
    void validate_WhenClauseValidationShouldBeSkippedForCreator_DoesNotValidateClause() {
        String creator = "creator";
        String reporter = "reporter";
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy(creator), Optional.of(new ValidationInput.ReportedBy(reporter)), List.of(), 5, 1234, "", Optional.empty());
        var drugClause = new DrugClause(new DrugClause.Drug(1L), Set.of(new DrugClause.Clause("0000", "clauses text")));
        var expression = Mockito.mock(BinaryExpression.class);
        var clause = new Clause(1L, drugClause.clauses().iterator().next().code(), null, new Clause.Error("", 10800), expression, null);
        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.of(drugClause));
        Mockito.when(clauseService.get(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(skippedValidationService.shouldSkipValidation(creator, validationInput.personId(), clause.id())).thenReturn(true);

        service.validate(validationInput);

        Mockito.verifyNoInteractions(expression);
    }

    @Test
    void validate_WhenClauseValidationShouldBeSkippedForReporter_DoesNotValidateClause() {
        String creator = "creator";
        String reporter = "reporter";
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy(creator), Optional.of(new ValidationInput.ReportedBy(reporter)), List.of(), 5, 1234, "", Optional.empty());
        var drugClause = new DrugClause(new DrugClause.Drug(1L), Set.of(new DrugClause.Clause("0000", "clauses text")));
        var expression = Mockito.mock(BinaryExpression.class);
        var clause = new Clause(1L, drugClause.clauses().iterator().next().code(), null, new Clause.Error("", 10800), expression, null);
        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.of(drugClause));
        Mockito.when(clauseService.get(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(skippedValidationService.shouldSkipValidation(creator, validationInput.personId(), clause.id())).thenReturn(false);
        Mockito.when(skippedValidationService.shouldSkipValidation(reporter, validationInput.personId(), clause.id())).thenReturn(true);

        service.validate(validationInput);

        Mockito.verifyNoInteractions(expression);
    }

    @Test
    void validate_WhenClauseValidationShouldNotBeSkippedForCreatorOrReporter_ValidatesClause() {
        String creator = "creator";
        String reporter = "reporter";
        var validationInput = new ValidationInput("1234", new ValidationInput.CreatedBy(creator), Optional.of(new ValidationInput.ReportedBy(reporter)), List.of(), 5, 1234, "", Optional.empty());
        var drugClause = new DrugClause(new DrugClause.Drug(1L), Set.of(new DrugClause.Clause("0000", "clauses text")));
        var expression = Mockito.mock(BinaryExpression.class);
        var clause = new Clause(1L, drugClause.clauses().iterator().next().code(), null, new Clause.Error("", 10800), expression, null);
        Mockito.when(drugClauseCache.get(validationInput.drugId())).thenReturn(Optional.of(drugClause));
        Mockito.when(clauseService.get(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(skippedValidationService.shouldSkipValidation(creator, validationInput.personId(), clause.id())).thenReturn(false);
        Mockito.when(skippedValidationService.shouldSkipValidation(reporter, validationInput.personId(), clause.id())).thenReturn(false);

        service.validate(validationInput);

        Mockito.verify(expression).validates(validationInput);
    }

}
