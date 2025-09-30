package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.StamData;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.common.repository.cache.ClauseCache;
import dk.kvalitetsit.itukt.common.repository.cache.StamdataCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @InjectMocks
    private ValidationServiceImpl service;
    @Mock
    private ClauseCache clauseCache;
    @Mock
    private StamdataCache stamDataCache;

    @Test
    void validate_WhenDrugIdDoesNotMatchClause_ReturnsSuccess() {
        var validationInput = new ValidationInput(5, 1234, "", Optional.empty());
        Mockito.when(stamDataCache.get(validationInput.drugId())).thenReturn(Optional.empty());

        var result = service.validate(validationInput);

        assertTrue(result.isEmpty());
    }

    @Test
    void validate_WhenClauseCacheDoesNotContainClauseForDrugId_ReturnsSuccess() {
        var validationInput = new ValidationInput(5, 1234, "", Optional.empty());
        var stamdataClause = new StamData(new StamData.Drug(1234L), Set.of(new StamData.Clause("0000", null)));
        Mockito.when(stamDataCache.get(validationInput.drugId())).thenReturn(Optional.of(stamdataClause));
        Mockito.when(clauseCache.get(stamdataClause.clauses().iterator().next().code())).thenReturn(Optional.empty());

        var result = service.validate(validationInput);

        assertTrue(result.isEmpty());
    }

    @Test
    void validate_WhenClauseCacheContainsClauseForDrugIdAndValidationSucceeds_ReturnsSuccess() {
        var validationInput = new ValidationInput(5, 1234, "", Optional.empty());
        var stamdataClause = new StamData(new StamData.Drug(1234L), Set.of(new StamData.Clause("0000", null)));
        var expression = Mockito.mock(BinaryExpression.class);
        var clause = new Clause(stamdataClause.clauses().iterator().next().code(), null, null, expression);

        Mockito.when(stamDataCache.get(validationInput.drugId())).thenReturn(Optional.of(stamdataClause));
        Mockito.when(clauseCache.get(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(expression.validates(validationInput)).thenReturn(true);

        var result = service.validate(validationInput);

        Mockito.verify(expression).validates(validationInput);
        assertTrue(result.isEmpty());
    }

    @Test
    void validate_WhenClauseCacheContainsClauseForDrugIdAndValidationFails_ReturnsValidationError() {
        var validationInput = new ValidationInput(5, 1234, "", Optional.empty());
        var stamdataClause = new StamData(new StamData.Drug(null), Set.of(new StamData.Clause("0000", "clauses text")));
        var expression = Mockito.mock(BinaryExpression.class);
        var clause = new Clause(stamdataClause.clauses().iterator().next().code(), null, 123, expression);
        Mockito.when(stamDataCache.get(validationInput.drugId())).thenReturn(Optional.of(stamdataClause));
        Mockito.when(clauseCache.get(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(expression.validates(validationInput)).thenReturn(false);

        var result = service.validate(validationInput);

        Mockito.verify(expression).validates(validationInput);

        assertEquals(1, result.size(), "Expected an error being returned");
        assertEquals(clause.name(), result.getFirst().clauseCode());
        assertEquals(stamdataClause.clauses().iterator().next().text(), result.getFirst().clauseText());
    }


    @Test
    void validate_WhenClauseCacheContainsMultipleClausesWhereTwoFails_ReturnsValidationErrors() {
        // Arrange
        var validationInput = new ValidationInput(5, 1234, "", Optional.empty());

        // two clauses in stamdata
        var stamdataClause = new StamData(
                new StamData.Drug(validationInput.drugId()),
                Set.of(
                        new StamData.Clause("0001", "clauses text"),
                        new StamData.Clause("0002", "clauses text"),
                        new StamData.Clause("0003", "clauses text")
                )
        );
        var clauses = stamdataClause.clauses().stream().toList();

        var succeedingExpression = Mockito.mock(BinaryExpression.class);
        Mockito.when(succeedingExpression.validates(validationInput)).thenReturn(true);

        var failingExpression = Mockito.mock(BinaryExpression.class);
        Mockito.when(failingExpression.validates(validationInput)).thenReturn(false);


        var clause_1 = new Clause(clauses.get(0).code(), null, null, failingExpression);
        var clause_2 = new Clause(clauses.get(1).code(), null, null, succeedingExpression);
        var clause_3 = new Clause(clauses.get(2).code(), null, null, failingExpression);

        Mockito.when(stamDataCache.get(validationInput.drugId())).thenReturn(Optional.of(stamdataClause));

        Mockito.when(clauseCache.get(clauses.get(0).code())).thenReturn(Optional.of(clause_1));
        Mockito.when(clauseCache.get(clauses.get(1).code())).thenReturn(Optional.of(clause_2));
        Mockito.when(clauseCache.get(clauses.get(2).code())).thenReturn(Optional.of(clause_3));

        var result = service.validate(validationInput);

        Mockito.verify(clauseCache, Mockito.times(clauses.size())).get(Mockito.any());

        assertEquals(2, result.size(), "Expected the validation to fail and two validation errors to be returned");

        assertEquals(clause_1.name(), result.get(0).clauseCode(), "Expected the name of the first failing clauses");
        assertEquals(clause_3.name(), result.get(1).clauseCode(), "Expected the name of the second failing clauses");
    }


}
