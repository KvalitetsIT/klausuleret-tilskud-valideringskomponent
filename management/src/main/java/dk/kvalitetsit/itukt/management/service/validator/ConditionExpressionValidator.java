package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.IndicationConditionExpression;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;

import java.util.List;

public class ConditionExpressionValidator implements ExpressionValidator<Expression.Condition> {
    private final ExpressionValidator<DepartmentSpecialityConditionExpression> departmentSpecialityExpressionValidator;
    private final ExpressionValidator<ExistingDrugMedicationConditionExpression> existingDrugMedicationExpressionValidator;
    private final ExpressionValidator<IndicationConditionExpression> indicationExpressionValidator;

    public ConditionExpressionValidator(ExpressionValidatorFactory expressionValidatorFactory) {
        this.departmentSpecialityExpressionValidator = expressionValidatorFactory.createDepartmentSpecialityExpressionValidator();
        this.existingDrugMedicationExpressionValidator = expressionValidatorFactory.createExistingDrugMedicationExpressionValidator();
        this.indicationExpressionValidator = expressionValidatorFactory.createIndicationExpressionValidator();
    }

    @Override
    public List<ExpressionValidationError> validate(Expression.Condition expression) {
        return switch (expression) {
            case DepartmentSpecialityConditionExpression exp -> departmentSpecialityExpressionValidator.validate(exp);
            case ExistingDrugMedicationConditionExpression exp -> existingDrugMedicationExpressionValidator.validate(exp);
            case IndicationConditionExpression exp -> indicationExpressionValidator.validate(exp);
            default -> List.of();
        };
    }
}
