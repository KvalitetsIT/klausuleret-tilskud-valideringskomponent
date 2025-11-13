package dk.kvalitetsit.itukt.common.model;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.*;

public record CreatedByConditionExpression(String speciality) implements Expression.Condition {
    @Override
    public Optional<ValidationFailed> validates(ValidationInput validationInput) {
        return Objects.equals(validationInput.createdBy().specialityCode(), Optional.of(speciality))
                ? empty()
                : Optional.of(new ValidationError.GenericError());
    }
}