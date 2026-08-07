package dk.kvalitetsit.itukt.management.service.model.validation;

import dk.kvalitetsit.itukt.common.model.Department;

import java.util.Set;

public record UnknownDepartmentSpecialityError(
        Department.Speciality departmentSpeciality,
        Set<Department.Speciality> knownSpecialities
) implements ExpressionValidationError {

    @Override
    public String errorMessage() {
        return "Unknown department speciality '%s'. Known specialities are: %s".formatted(
                departmentSpeciality.name(),
                knownSpecialities.stream().map(Department.Speciality::name).sorted().toList());
    }
}
