package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public class AndErrorMapper implements ValidationErrorMapper<ValidationError.AndError> {
    private final ValidationErrorMapper<ValidationError> combinedValidationErrorMapper;

    public AndErrorMapper(ValidationErrorMapper<ValidationError> combinedValidationErrorMapper) {
        this.combinedValidationErrorMapper = combinedValidationErrorMapper;
    }

    @Override
    public String map(ValidationError.AndError error) {
        return parenthesesIfNecessary(error.e1()) + " og " + parenthesesIfNecessary(error.e2());
    }

    private String parenthesesIfNecessary(ValidationError error) {
        String errorMessage = combinedValidationErrorMapper.map(error);
        return error instanceof ValidationError.OrError ? "(" + errorMessage + ")" : errorMessage;
    }
}
