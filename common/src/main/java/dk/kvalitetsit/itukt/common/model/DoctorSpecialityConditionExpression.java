package dk.kvalitetsit.itukt.common.model;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.*;

/**
 * Expression that validates the speciality code (created by) in a {@link ValidationInput} instance
 */
public record DoctorSpecialityConditionExpression(String speciality) implements Expression.Condition {
    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        return Objects.equals(validationInput.createdBy().specialityCode(), Optional.of(speciality))
                ? empty()
                : Optional.of(new ValidationError.ConditionError(ValidationError.Field.DOCTOR_SPECIALITY, Operator.EQUAL, speciality));
    }
}