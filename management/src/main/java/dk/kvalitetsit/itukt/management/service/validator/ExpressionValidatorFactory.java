package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.service.DepartmentSpecialityService;
import dk.kvalitetsit.itukt.common.service.MedicationATCService;
import dk.kvalitetsit.itukt.common.service.MedicationFormService;

public class ExpressionValidatorFactory {
    private final DepartmentSpecialityService departmentSpecialityService;
    private final MedicationFormService medicationFormService;
    private final MedicationATCService medicationATCService;

    public ExpressionValidatorFactory(
            DepartmentSpecialityService departmentSpecialityService,
            MedicationFormService medicationFormService,
            MedicationATCService medicationATCService
    ) {
        this.departmentSpecialityService = departmentSpecialityService;
        this.medicationFormService = medicationFormService;
        this.medicationATCService = medicationATCService;
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

    public ExpressionValidator<ExistingDrugMedicationConditionExpression> createExistingDrugMedicationExpressionValidator() {
        return new ExistingDrugMedicationExpressionValidator(medicationFormService, medicationATCService);
    }
}
