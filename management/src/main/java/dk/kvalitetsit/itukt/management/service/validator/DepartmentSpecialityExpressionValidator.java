package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;

import java.util.List;

public class DepartmentSpecialityExpressionValidator implements ExpressionValidator<DepartmentSpecialityConditionExpression> {
    private final StamdataCacheService<Department.Speciality> departmentSpecialityService;

    public DepartmentSpecialityExpressionValidator(StamdataCacheService<Department.Speciality> departmentSpecialityService) {
        this.departmentSpecialityService = departmentSpecialityService;
    }

    @Override
    public List<ExpressionValidationError> validate(DepartmentSpecialityConditionExpression expression) {
        var speciality = departmentSpecialityService.get(expression.requiredSpeciality());
        return speciality.isPresent() ? List.of()
                : List.of(
                new UnknownValueError(
                        Identifier.DEPARTMENT_SPECIALITY,
                        expression.requiredSpeciality()
                ));
    }
}
