package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationResult;
import org.openapitools.model.*;

import java.util.List;
import java.util.stream.Stream;

/**
 * The {@code ValidationServiceAdaptor} class is responsible for adapting between the boundary layer and the service layer {@link ValidationServiceImpl}.
 * <p>
 * This class accommodates isolation in terms of testing as mocking mappers will be avoided
 */
public class ValidationServiceAdaptor implements ValidationService<ValidationRequest, ValidationResponse> {

    private final ValidationService<ValidationInput, ValidationResult> validationService;

    public ValidationServiceAdaptor(ValidationService<ValidationInput, ValidationResult> validationService) {
        this.validationService = validationService;
    }

    @Override
    public ValidationResponse validate(ValidationRequest request) {
        var responses = request.getValidate().stream()
                .map(validate -> validate(request, validate))
                .toList();
        return combineResponses(responses);
    }

    private ValidationResponse validate(ValidationRequest request, Validate validate) {
        var validationInput = mapToValidationInput(request, validate);
        var result = validationService.validate(validationInput);
        return switch (result) {
            case dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess ignored -> new ValidationSuccess();
            case dk.kvalitetsit.itukt.validation.service.model.ValidationError error -> mapValidationError(validate, error);
        };
    }

    private ValidationResponse combineResponses(List<ValidationResponse> responses) {
        if (responses.stream().allMatch(resp -> resp instanceof ValidationSuccess)) {
            return new ValidationSuccess();
        }

        var validationErrors = responses.stream()
                .flatMap(resp -> resp instanceof ValidationFailed failed ?
                        failed.getValidationErrors().stream() : Stream.of())
                .toList();
        return new ValidationFailed().validationErrors(validationErrors);
    }

    private ValidationResponse mapValidationError(Validate validateInput, dk.kvalitetsit.itukt.validation.service.model.ValidationError modelValidationError) {
        var validationError = new ValidationError()
                .errorCode(0)
                .errorMessage(modelValidationError.errorMessage())
                .elementPath(validateInput.getElementPath())
                .clauseCode(modelValidationError.clauseCode())
                .clauseText(modelValidationError.clauseText())
                .warningQuestion("TODO: IUAKT-78");
        return new ValidationFailed()
                .addValidationErrorsItem(validationError);
    }

    private ValidationInput mapToValidationInput(ValidationRequest validationRequest, Validate validate) {
        return new ValidationInput(
                validationRequest.getAge(),
                validate.getNewDrugMedication().getDrugIdentifier());
    }
}
