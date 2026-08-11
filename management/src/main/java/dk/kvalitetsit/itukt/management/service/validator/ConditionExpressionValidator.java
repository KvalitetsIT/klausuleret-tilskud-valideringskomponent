package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;

import java.util.List;

public class ConditionExpressionValidator implements ExpressionValidator<Expression.Condition> {
    private final ExpressionValidator<DepartmentSpecialityConditionExpression> departmentSpecialityExpressionValidator;

    public ConditionExpressionValidator(ExpressionValidatorFactory expressionValidatorFactory) {
        this.departmentSpecialityExpressionValidator = expressionValidatorFactory.createDepartmentSpecialityExpressionValidator();
    }

    @Override
    public List<ExpressionValidationError> validate(Expression.Condition expression) {
        return switch (expression) {
            case DepartmentSpecialityConditionExpression exp -> departmentSpecialityExpressionValidator.validate(exp);
            default -> List.of();
        };
    }
}
