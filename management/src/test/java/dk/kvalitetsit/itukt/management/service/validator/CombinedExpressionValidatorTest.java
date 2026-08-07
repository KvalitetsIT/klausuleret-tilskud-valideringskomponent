package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.AgeConditionExpression;
import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownDepartmentSpecialityError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CombinedExpressionValidatorTest {

    @Mock
    private ExpressionValidatorFactory expressionValidatorFactory;

    @Mock
    private ExpressionValidator<BinaryExpression> binaryExpressionValidator;

    @Mock
    private ExpressionValidator<Expression.Condition> conditionExpressionValidator;

    private CombinedExpressionValidator validator;

    @BeforeEach
    void setUp() {
        when(expressionValidatorFactory.createBinaryExpressionValidator(Mockito.any())).thenReturn(binaryExpressionValidator);
        when(expressionValidatorFactory.createConditionExpressionValidator()).thenReturn(conditionExpressionValidator);
        validator = new CombinedExpressionValidator(expressionValidatorFactory);
    }

    @Test
    void validate_WhenExpressionIsBinary_DelegatesToBinaryExpressionValidator() {
        var binaryExpression = Mockito.mock(BinaryExpression.class);
        var error = Mockito.mock(UnknownDepartmentSpecialityError.class);
        when(binaryExpressionValidator.validate(binaryExpression)).thenReturn(List.of(error));

        var result = validator.validate(binaryExpression);

        assertEquals(List.of(error), result);
    }

    @Test
    void validate_WhenExpressionIsCondition_DelegatesToConditionExpressionValidator() {
        var condition = Mockito.mock(AgeConditionExpression.class);
        var error = Mockito.mock(UnknownDepartmentSpecialityError.class);
        when(conditionExpressionValidator.validate(condition)).thenReturn(List.of(error));

        var result = validator.validate(condition);

        assertEquals(List.of(error), result);
    }
}



