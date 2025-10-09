package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

public record StringConditionExpression(Field field, String requiredValue) implements Expression.Condition {
    @Override
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        var fieldValue = validationInput.getByField(field());
        return requiredValue.equals(fieldValue)
                ? Optional.empty()
                : Optional.of(toError(field, requiredValue));
    }

    private ValidationError toError(Field field, String value) {
        return switch(field) {
            case AGE -> new SpecificError(ValidationError.Field.AGE, Operator.EQUAL, value);
            case INDICATION -> new SpecificError(ValidationError.Field.INDICATION, Operator.EQUAL, value);
            case EXISTING_DRUG_MEDICATION -> new UnsupportedError();
        };
    }
}