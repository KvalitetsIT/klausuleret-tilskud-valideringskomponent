package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;

public class ExpressionValidatorFactory {
    private final StamdataCacheService<Department.Speciality> departmentSpecialityService;
    private final StamdataCacheService<Medication.Form> medicationFormService;
    private final StamdataCacheService<Medication.ATC> medicationATCService;
    private final StamdataCacheService<Indication> indicationService;
    private final StamdataCacheService<Medication.Route> medicationRouteService;

    public ExpressionValidatorFactory(
            StamdataCacheService<Department.Speciality> departmentSpecialityService,
            StamdataCacheService<Medication.Form> medicationFormService,
            StamdataCacheService<Medication.ATC> medicationATCService,
            StamdataCacheService<Indication> indicationService,
            StamdataCacheService<Medication.Route> medicationRouteService
    ) {
        this.departmentSpecialityService = departmentSpecialityService;
        this.medicationFormService = medicationFormService;
        this.medicationATCService = medicationATCService;
        this.indicationService = indicationService;
        this.medicationRouteService = medicationRouteService;
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
        return new ExistingDrugMedicationExpressionValidator(medicationFormService, medicationATCService, medicationRouteService);
    }

    public ExpressionValidator<IndicationConditionExpression> createIndicationExpressionValidator() {
        return new IndicationExpressionValidator(indicationService);
    }
}
