package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.PrescriptionValidationError;
import dk.kvalitetsit.itukt.common.model.ValidationError;

public class PrefixedErrorMessageAssembler implements ErrorMessageAssembler<PrescriptionValidationError> {
    private static final String PREFIX = "Manglende opfyldelse af tilskudsklausulen for '%s': ";
    private final ErrorMessageAssembler<ValidationError> errorMessageAssembler;

    public PrefixedErrorMessageAssembler(ErrorMessageAssembler<ValidationError> errorMessageAssembler) {
        this.errorMessageAssembler = errorMessageAssembler;
    }

    @Override
    public String assemble(PrescriptionValidationError error) {
        return PREFIX.formatted(error.drugName()) + errorMessageAssembler.assemble(error.content());
    }
}
