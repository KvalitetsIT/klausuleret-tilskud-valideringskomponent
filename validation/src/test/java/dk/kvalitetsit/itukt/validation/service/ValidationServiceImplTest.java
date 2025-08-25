package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataCache;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;
import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
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
    @Mock
    private Mapper<ValidationInput, DataContext> validationDataContextMapper;
    @Mock
    private Evaluator evaluator;

    @Test
    void validate_WhenDrugIdDoesNotMatchClause_ReturnsSuccess() {
        var validationInput = new ValidationInput(5, 1234);
        Mockito.when(stamDataCache.getClauseCodeByDrugId(validationInput.drugId())).thenReturn(Optional.empty());

        var result = service.validate(validationInput);

        assertInstanceOf(ValidationSuccess.class, result);
    }

    @Test
    void validate_WhenClauseCacheDoesNotContainClauseForDrugId_ReturnsSuccess() {
        var validationInput = new ValidationInput(5, 1234);
        String clauseCode = "0000";
        Mockito.when(stamDataCache.getClauseCodeByDrugId(validationInput.drugId())).thenReturn(Optional.of(clauseCode));
        Mockito.when(clauseCache.getClause(clauseCode)).thenReturn(Optional.empty());

        var result = service.validate(validationInput);

        assertInstanceOf(ValidationSuccess.class, result);
    }

    @Test
    void validate_WhenClauseCacheContainsClauseForDrugIdAndValidationSucceeds_ReturnsSuccess() {
        var validationInput = new ValidationInput(5, 1234);
        var clause = new Clause("0000", Optional.empty(), Mockito.mock(Expression.BinaryExpression.class));
        var dataContext = Mockito.mock(DataContext.class);
        Mockito.when(stamDataCache.getClauseCodeByDrugId(validationInput.drugId())).thenReturn(Optional.of(clause.name()));
        Mockito.when(clauseCache.getClause(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(validationDataContextMapper.map(validationInput)).thenReturn(dataContext);
        Mockito.when(evaluator.eval(clause.expression(), dataContext)).thenReturn(true);

        var result = service.validate(validationInput);

        Mockito.verify(evaluator).eval(clause.expression(), dataContext);
        assertInstanceOf(ValidationSuccess.class, result);
    }

    @Test
    void validate_WhenClauseCacheContainsClauseForDrugIdAndValidationFails_ReturnsValidationError() {
        var validationInput = new ValidationInput(5, 1234);
        var clause = new Clause("0000", Optional.empty(), Mockito.mock(Expression.BinaryExpression.class));
        var dataContext = Mockito.mock(DataContext.class);
        Mockito.when(stamDataCache.getClauseCodeByDrugId(validationInput.drugId())).thenReturn(Optional.of(clause.name()));
        Mockito.when(clauseCache.getClause(clause.name())).thenReturn(Optional.of(clause));
        Mockito.when(validationDataContextMapper.map(validationInput)).thenReturn(dataContext);
        Mockito.when(evaluator.eval(clause.expression(), dataContext)).thenReturn(false);

        var result = service.validate(validationInput);

        Mockito.verify(evaluator).eval(clause.expression(), dataContext);
        assertInstanceOf(ValidationError.class, result);
        ValidationError validationError = (ValidationError) result;
        assertEquals(clause.name(), validationError.clauseCode());
    }

}
