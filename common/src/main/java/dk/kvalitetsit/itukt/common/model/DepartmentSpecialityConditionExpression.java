package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.Set;

public record DepartmentSpecialityConditionExpression(String requiredSpeciality) implements Expression.Condition {

    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        var reportedBySpecialities = validationInput.reportedBy()
                .flatMap(ValidationInput.Actor::department)
                .map(Department::specialities)
                .orElse(Set.of());
        var createdBySpecialities = validationInput.createdBy().department()
                .map(Department::specialities)
                .orElse(Set.of());

        var createdByFailure = validate(reportedBySpecialities);
        var reportedByFailure = validate(createdBySpecialities);

        return reportedByFailure.isEmpty() ? Optional.empty() : createdByFailure;
    }

    private Optional<ValidationFailed> validate(Set<Department.Speciality> specialities) {
        if (specialities.contains(new Department.Speciality(requiredSpeciality))) return Optional.empty();
        return Optional.of(new ValidationError.ConditionError(
                ValidationError.ConditionError.Field.DEPARTMENT_SPECIALITY,
                Operator.EQUAL,
                requiredSpeciality
        ));
    }
}