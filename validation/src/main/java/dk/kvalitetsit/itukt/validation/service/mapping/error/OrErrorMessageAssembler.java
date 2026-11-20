package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public class OrErrorMessageAssembler implements ErrorMessageAssembler<ValidationError.OrError> {
    private final ErrorMessageAssembler<ValidationError> combinedErrorMessageAssembler;

    public OrErrorMessageAssembler(ErrorMessageAssembler<ValidationError> combinedErrorMessageAssembler) {
        this.combinedErrorMessageAssembler = combinedErrorMessageAssembler;
    }

    @Override
    public String assemble(ValidationError.OrError error) {
        return combinedErrorMessageAssembler.assemble(error.e1()) + " eller " + combinedErrorMessageAssembler.assemble(error.e2());
    }
}
