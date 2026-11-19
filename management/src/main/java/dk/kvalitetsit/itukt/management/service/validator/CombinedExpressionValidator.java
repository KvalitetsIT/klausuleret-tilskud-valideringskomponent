package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;

import java.util.List;

public class CombinedExpressionValidator implements ExpressionValidator<Expression> {
    private final ExpressionValidator<BinaryExpression> binaryExpressionValidator;
    private final ExpressionValidator<Expression.Condition> conditionExpressionValidator;

    public CombinedExpressionValidator(ExpressionValidatorFactory expressionValidatorFactory) {
        this.binaryExpressionValidator = expressionValidatorFactory.createBinaryExpressionValidator(this);
        this.conditionExpressionValidator = expressionValidatorFactory.createConditionExpressionValidator();
    }

    @Override
    public List<ExpressionValidationError> validate(Expression expression) {
        return switch (expression) {
            case BinaryExpression binaryExpression -> binaryExpressionValidator.validate(binaryExpression);
            case Expression.Condition condition -> conditionExpressionValidator.validate(condition);
        };
    }
}
