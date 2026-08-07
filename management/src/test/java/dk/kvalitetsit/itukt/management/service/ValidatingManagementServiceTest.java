package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.common.model.AgeConditionExpression;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;
import dk.kvalitetsit.itukt.management.exceptions.RequiresForceException;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseUpdateInput;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownDepartmentSpecialityError;
import dk.kvalitetsit.itukt.management.service.validator.ExpressionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidatingManagementServiceTest {
    @Mock
    private ManagementService managementService;

    @Mock
    private ExpressionValidator<Expression> expressionValidator;

    @InjectMocks
    private ValidatingManagementService validatingManagementService;

    @Test
    void create_WhenExpressionContainsValidationErrors_ThrowsException() {
        var input = Mockito.mock(ClauseInput.class);
        Mockito.when(input.expression()).thenReturn(Mockito.mock(AgeConditionExpression.class));
        List<ExpressionValidationError> errors = List.of(Mockito.mock(UnknownDepartmentSpecialityError.class));
        Mockito.when(expressionValidator.validate(input.expression())).thenReturn(errors);

        var e = assertThrows(RequiresForceException.class, () -> validatingManagementService.create(input));

        assertEquals(errors, e.getValidationErrors());
    }

    @Test
    void create_WhenExpressionContainsNoErrors_DelegatesToManagementService() throws ManagementException {
        var input = Mockito.mock(ClauseInput.class);
        Mockito.when(input.expression()).thenReturn(Mockito.mock(AgeConditionExpression.class));
        Mockito.when(expressionValidator.validate(input.expression())).thenReturn(List.of());
        var expectedClause = Mockito.mock(Clause.class);
        Mockito.when(managementService.create(input)).thenReturn(expectedClause);

        var result = validatingManagementService.create(input);

        assertEquals(expectedClause, result);
    }

    @Test
    void updateDraft_WhenExpressionContainsValidationErrors_ThrowsException() {
        var input = Mockito.mock(ClauseUpdateInput.class);
        Mockito.when(input.expression()).thenReturn(Mockito.mock(AgeConditionExpression.class));
        List<ExpressionValidationError> errors = List.of(Mockito.mock(UnknownDepartmentSpecialityError.class));
        Mockito.when(expressionValidator.validate(input.expression())).thenReturn(errors);

        var e = assertThrows(RequiresForceException.class, () -> validatingManagementService.updateDraft("", input));

        assertEquals(errors, e.getValidationErrors());
    }

    @Test
    void updateDraft_WhenExpressionContainsNoErrors_DelegatesToManagementService() throws ManagementException {
        var input = Mockito.mock(ClauseUpdateInput.class);
        Mockito.when(input.expression()).thenReturn(Mockito.mock(AgeConditionExpression.class));
        Mockito.when(expressionValidator.validate(input.expression())).thenReturn(List.of());
        var expectedClause = Mockito.mock(Clause.class);
        String name = "test";
        Mockito.when(managementService.updateDraft(name, input)).thenReturn(expectedClause);

        var result = validatingManagementService.updateDraft(name, input);

        assertEquals(expectedClause, result);
    }
}