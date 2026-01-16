package dk.kvalitetsit.itukt.common.model;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Expression that validates the speciality code (createdBy or reportedBy by) in a {@link ValidationInput} instance
 */
public record DoctorSpecialityConditionExpression(String speciality) implements Expression.Condition {
    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        var equals = Objects.equals(validationInput.createdBy().specialityCode(), of(speciality)) ||
                Objects.equals(validationInput.reportedBy().flatMap(ValidationInput.Actor::specialityCode), of(speciality));
        return equals
                ? empty()
                : Optional.of(new ValidationError.ConditionError(ValidationError.ConditionError.Field.DOCTOR_SPECIALITY, Operator.EQUAL, speciality));
    }
}