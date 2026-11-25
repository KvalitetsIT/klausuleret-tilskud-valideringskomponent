package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ValidationError;

public class PrefixedErrorMessageAssembler {
    private static final String PREFIX = "Manglende opfyldelse af tilskudsklausulen for '%s': ";
    private final ErrorMessageAssembler<ValidationError> errorMessageAssembler;

    public PrefixedErrorMessageAssembler(ErrorMessageAssembler<ValidationError> errorMessageAssembler) {
        this.errorMessageAssembler = errorMessageAssembler;
    }

    public String assemble(ValidationError error, String drugName) {
        return PREFIX.formatted(drugName) + errorMessageAssembler.assemble(error);
    }
}
