package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public class AndErrorMessageAssembler implements ErrorMessageAssembler<ValidationError.AndError> {
    private final ErrorMessageAssembler<ValidationError> combinedErrorMessageAssembler;

    public AndErrorMessageAssembler(ErrorMessageAssembler<ValidationError> combinedErrorMessageAssembler) {
        this.combinedErrorMessageAssembler = combinedErrorMessageAssembler;
    }

    @Override
    public String assemble(ValidationError.AndError error) {
        return parenthesesIfNecessary(error.e1()) + " og " + parenthesesIfNecessary(error.e2());
    }

    private String parenthesesIfNecessary(ValidationError error) {
        String errorMessage = combinedErrorMessageAssembler.assemble(error);
        return error instanceof ValidationError.OrError ? "(" + errorMessage + ")" : errorMessage;
    }
}
