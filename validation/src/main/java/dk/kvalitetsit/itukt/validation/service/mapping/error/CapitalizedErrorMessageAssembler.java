package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;
import org.apache.commons.lang3.StringUtils;

public class CapitalizedErrorMessageAssembler implements ErrorMessageAssembler<ValidationError> {
    private final ErrorMessageAssembler<ValidationError> delegate;

    public CapitalizedErrorMessageAssembler(ErrorMessageAssembler<ValidationError> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String assemble(ValidationError error) {
        String errorMessage = delegate.assemble(error);
        return StringUtils.capitalize(errorMessage);
    }
}
