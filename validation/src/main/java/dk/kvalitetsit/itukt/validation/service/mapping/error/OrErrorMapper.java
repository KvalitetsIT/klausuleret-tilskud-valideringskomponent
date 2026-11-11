package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public class OrErrorMapper implements ValidationErrorMapper<ValidationError.OrError> {
    private final ValidationErrorMapper<ValidationError> validationErrorMapper;

    public OrErrorMapper(ValidationErrorMapper<ValidationError> validationErrorMapper) {
        this.validationErrorMapper = validationErrorMapper;
    }

    @Override
    public String map(ValidationError.OrError error) {
        return validationErrorMapper.map(error.e1()) + " eller " + validationErrorMapper.map(error.e2());
    }
}
