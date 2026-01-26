package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

import static java.util.Optional.empty;

/**
 * Expression that validates the speciality code (createdBy or reportedBy by) in a {@link ValidationInput} instance
 */
public record DoctorSpecialityConditionExpression(String speciality) implements Expression.Condition {
    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        boolean createdByMatchesCondition = validationInput.createdBy().specialityCode().stream()
                .anyMatch(speciality::equalsIgnoreCase);
        boolean reportedByMatchesCondition = validationInput.reportedBy().flatMap(ValidationInput.Actor::specialityCode).stream()
                .anyMatch(speciality::equalsIgnoreCase);

        return createdByMatchesCondition || reportedByMatchesCondition
                ? empty()
                : Optional.of(new ValidationError.ConditionError(ValidationError.ConditionError.Field.DOCTOR_SPECIALITY, Operator.EQUAL, speciality));
    }
}