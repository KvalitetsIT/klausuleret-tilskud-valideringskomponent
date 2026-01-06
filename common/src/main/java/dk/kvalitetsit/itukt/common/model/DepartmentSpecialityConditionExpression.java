package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record DepartmentSpecialityConditionExpression(String requiredSpeciality) implements Expression.Condition {

    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        var reportedBy = validationInput.reportedBy().flatMap(ValidationInput.Actor::department);
        var createdBy = validationInput.createdBy().department();

        var createdByFailure = createdBy.flatMap(d -> validate(d.specialities()));
        var reportedByFailure = reportedBy.flatMap(d -> validate(d.specialities()));

        return createdByFailure.isEmpty() || reportedByFailure.isEmpty() ? Optional.empty() : createdByFailure;
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