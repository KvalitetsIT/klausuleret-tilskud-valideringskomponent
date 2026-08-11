package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.service.DepartmentSpecialityService;

public class ExpressionValidatorFactory {
    private final DepartmentSpecialityService departmentSpecialityService;

    public ExpressionValidatorFactory(DepartmentSpecialityService departmentSpecialityService) {
        this.departmentSpecialityService = departmentSpecialityService;
    }

    public ExpressionValidator<Expression> createCombinedExpressionValidator() {
        return new CombinedExpressionValidator(this);
    }

    public ExpressionValidator<BinaryExpression> createBinaryExpressionValidator(ExpressionValidator<Expression> expressionValidator) {
        return new BinaryExpressionValidator(expressionValidator);
    }

    public ExpressionValidator<Expression.Condition> createConditionExpressionValidator() {
        return new ConditionExpressionValidator(this);
    }

    public ExpressionValidator<DepartmentSpecialityConditionExpression> createDepartmentSpecialityExpressionValidator() {
        return new DepartmentSpecialityExpressionValidator(departmentSpecialityService);
    }
}
