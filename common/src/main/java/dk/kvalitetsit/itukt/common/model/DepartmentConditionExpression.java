package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record DepartmentConditionExpression(String requiredSpeciality) implements Expression.Condition {

    public Optional<ValidationFailed> validates(ValidationInput validationInput) {

        var reportedBy = validationInput.reportedBy();
        var createdBy = validationInput.createdById();

        return reportedBy
                .flatMap(ValidationInput.Actor::department)
                .flatMap(department -> validate(department.specialities(), requiredSpeciality))
                .or(() -> createdBy.department().flatMap(department -> this.validate(department.specialities(), requiredSpeciality)));
    }

    private Optional<ValidationFailed> validate(Set<Speciality> specialities, String requiredSpeciality) {
        ValidationError.ConditionError error = new ValidationError.ConditionError(ValidationError.ConditionError.Field.DEPARTMENT_SPECIALITY, Operator.EQUAL, requiredSpeciality);
        var s = specialities.stream().map(Speciality::name).collect(Collectors.toSet());
        return s.contains(requiredSpeciality) ? Optional.empty() : Optional.of(error);
    }
}