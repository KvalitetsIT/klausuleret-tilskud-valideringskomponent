package dk.kvalitetsit.itukt.management.service.validator;

import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.DepartmentSpecialityConditionExpression;
import dk.kvalitetsit.itukt.common.service.DepartmentSpecialityService;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;
import dk.kvalitetsit.itukt.management.service.model.validation.UnknownValueError;

import java.util.List;
import java.util.stream.Collectors;

public class DepartmentSpecialityExpressionValidator implements ExpressionValidator<DepartmentSpecialityConditionExpression> {
    private final DepartmentSpecialityService departmentSpecialityService;

    public DepartmentSpecialityExpressionValidator(DepartmentSpecialityService departmentSpecialityService) {
        this.departmentSpecialityService = departmentSpecialityService;
    }

    @Override
    public List<ExpressionValidationError> validate(DepartmentSpecialityConditionExpression expression) {
        var speciality = departmentSpecialityService.getSpeciality(expression.requiredSpeciality());
        return speciality.isPresent() ? List.of()
                : List.of(
                new UnknownValueError(
                        Identifier.DEPARTMENT_SPECIALITY,
                        expression.requiredSpeciality(),
                        departmentSpecialityService.getSpecialities().stream().map(Department.Speciality::name).collect(Collectors.toSet())
                ));
    }
}
