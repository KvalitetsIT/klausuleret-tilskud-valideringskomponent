package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.IndicationConditionExpression;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownDepartmentSpecialityError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BinaryExpressionValidatorTest {

    @Mock
    private ExpressionValidator<Expression> expressionValidator;

    @InjectMocks
    private BinaryExpressionValidator validator;

    @Test
    void validate_CombinesErrorsFromBothSides() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(IndicationConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.AND, right);
        var leftError = Mockito.mock(UnknownDepartmentSpecialityError.class);
        var rightError = Mockito.mock(UnknownDepartmentSpecialityError.class);
        when(expressionValidator.validate(left)).thenReturn(List.of(leftError));
        when(expressionValidator.validate(right)).thenReturn(List.of(rightError));

        var result = validator.validate(binaryExpression);

        assertEquals(List.of(leftError, rightError), result);
    }

    @Test
    void validate_WhenBothSidesAreValid_ReturnsNoErrors() {
        var left = Mockito.mock(IndicationConditionExpression.class);
        var right = Mockito.mock(IndicationConditionExpression.class);
        var binaryExpression = new BinaryExpression(left, BinaryExpression.Operator.OR, right);
        when(expressionValidator.validate(left)).thenReturn(List.of());
        when(expressionValidator.validate(right)).thenReturn(List.of());

        var result = validator.validate(binaryExpression);

        assertEquals(List.of(), result);
    }
}

