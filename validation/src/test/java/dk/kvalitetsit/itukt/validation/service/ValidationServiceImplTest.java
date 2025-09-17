package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataCache;
import dk.kvalitetsit.itukt.validation.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;
import dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @InjectMocks
    private ValidationServiceImpl service;
    @Mock
    private ClauseCache clauseCache;
    @Mock
    private StamDataCache stamDataCache;

    @Test
    void validate_WhenDrugIdDoesNotMatchClause_ReturnsSuccess() {
        var validationInput = new ValidationInput(5, 1234, "", Optional.empty());
        Mockito.when(stamDataCache.getClauseByDrugId(validationInput.drugId())).thenReturn(Optional.empty());

        var result = service.validate(validationInput);

        assertInstanceOf(ValidationSuccess.class, result);
    }

    @Test
    void validate_WhenClauseCacheDoesNotContainClauseForDrugId_ReturnsSuccess() {
        var validationInput = new ValidationInput(5, 1234, "", Optional.empty());
        var stamdataClause = new ClauseEntity("0000", null, null, null, null, null, null);
        Mockito.when(stamDataCache.getClauseByDrugId(validationInput.drugId())).thenReturn(Optional.of(stamdataClause));
        Mockito.when(clauseCache.getClause(stamdataClause.kode())).thenReturn(Optional.empty());

        var result = service.validate(validationInput);

        assertInstanceOf(ValidationSuccess.class, result);
    }

    @Test
    void validate_WhenClauseCacheContainsClauseForDrugIdAndValidationSucceeds_ReturnsSuccess() {
        var validationInput = new ValidationInput(5, 1234, "", Optional.empty());
        var stamdataClause = new ClauseEntity("0000", null, null, null, null, null, null);
        var expression = Mockito.mock(BinaryExpression.class);
        var clause = new Clause(stamdataClause.kode(), null, expression);
        Mockito.when(stamDataCache.getClauseByDrugId(validationInput.drugId())).thenReturn(Optional.of(stamdataClause));
        Mockito.when(clauseCache.getClause(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(expression.validates(validationInput)).thenReturn(true);


        var result = service.validate(validationInput);

        Mockito.verify(expression).validates(validationInput);
        assertInstanceOf(ValidationSuccess.class, result);
    }

    @Test
    void validate_WhenClauseCacheContainsClauseForDrugIdAndValidationFails_ReturnsValidationError() {
        var validationInput = new ValidationInput(5, 1234, "", Optional.empty());
        var stamdataClause = new ClauseEntity("0000", null, null, null, null, "clause text", null);
        var expression = Mockito.mock(BinaryExpression.class);
        var clause = new Clause(stamdataClause.kode(), null, expression);
        Mockito.when(stamDataCache.getClauseByDrugId(validationInput.drugId())).thenReturn(Optional.of(stamdataClause));
        Mockito.when(clauseCache.getClause(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(expression.validates(validationInput)).thenReturn(false);

        var result = service.validate(validationInput);

        Mockito.verify(expression).validates(validationInput);
        assertInstanceOf(ValidationError.class, result);
        ValidationError validationError = (ValidationError) result;
        assertEquals(clause.name(), validationError.clauseCode());
        assertEquals(stamdataClause.tekst(), validationError.clauseText());
    }

}
