package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record DepartmentConditionExpression(String requiredSpeciality) implements Expression.Condition {

    public Optional<ValidationFailed> validates(ValidationInput validationInput) {

        var reportedBy = validationInput.reportedBy().flatMap(ValidationInput.Actor::department);
        var createdBy = validationInput.createdBy().department();

        var e = createdBy.map(d -> validate(d.specialities(), requiredSpeciality));

        var b = reportedBy.flatMap(d -> validate(d.specialities(), requiredSpeciality));

        return e.isEmpty() || b.isEmpty() ? Optional.empty() : e.get();

    }


    private Optional<ValidationFailed> validate(Set<Department.Speciality> specialities, String requiredSpeciality) {
        ValidationError.ConditionError error = new ValidationError.ConditionError(ValidationError.ConditionError.Field.DEPARTMENT_SPECIALITY, Operator.EQUAL, requiredSpeciality);
        var s = specialities.stream().map(Department.Speciality::name).collect(Collectors.toSet());
        return s.contains(requiredSpeciality) ? Optional.empty() : Optional.of(error);
    }
}