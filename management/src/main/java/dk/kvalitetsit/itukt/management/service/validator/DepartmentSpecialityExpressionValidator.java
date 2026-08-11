package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.service.DepartmentSpecialityService;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownDepartmentSpecialityError;

import java.util.List;

public class DepartmentSpecialityExpressionValidator implements ExpressionValidator<DepartmentSpecialityConditionExpression> {
    private final DepartmentSpecialityService departmentSpecialityService;

    public DepartmentSpecialityExpressionValidator(DepartmentSpecialityService departmentSpecialityService) {
        this.departmentSpecialityService = departmentSpecialityService;
    }

    @Override
    public List<ExpressionValidationError> validate(DepartmentSpecialityConditionExpression expression) {
        var speciality = departmentSpecialityService.getSpeciality(expression.requiredSpeciality());
        return speciality.isPresent() ? List.of()
                : List.of(new UnknownDepartmentSpecialityError(expression.requiredSpeciality(), departmentSpecialityService.getSpecialities()));
    }
}
