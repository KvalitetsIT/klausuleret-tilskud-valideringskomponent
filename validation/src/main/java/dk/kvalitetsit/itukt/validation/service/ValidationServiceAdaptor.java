package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationResult;
import org.openapitools.model.*;

import java.util.Optional;

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
        try {
            return validateAll(request);
        } catch (ExistingDrugMedicationRequiredException e) {
            return new ValidationNotPossible().reason(ValidationNotPossible.ReasonEnum.EXISTING_DRUG_MEDICATIONS_REQUIRED);
        }
    }

    private ValidationResponse validateAll(ValidationRequest request) {
        var validationErrors = request.getValidate().stream()
                .flatMap(validate -> validate(request, validate).stream())
                .toList();
        return validationErrors.isEmpty() ?
                new ValidationSuccess() :
                new ValidationFailed().validationErrors(validationErrors);
    }

    private Optional<ValidationError> validate(ValidationRequest request, Validate validate) {
        var validationInput = mapToValidationInput(request, validate);
        var result = validationService.validate(validationInput);
        return switch (result) {
            case dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess ignored -> Optional.empty();
            case dk.kvalitetsit.itukt.validation.service.model.ValidationError error ->
                    Optional.of(mapValidationError(validate, error));
        };
    }

    private ValidationError mapValidationError(Validate validateInput, dk.kvalitetsit.itukt.validation.service.model.ValidationError modelValidationError) {
        return new ValidationError()
                .errorCode(modelValidationError.errorCode())
                .errorMessage(modelValidationError.errorMessage())
                .elementPath(validateInput.getElementPath())
                .clauseCode(modelValidationError.clauseCode())
                .clauseText(modelValidationError.clauseText())
                .warningQuestion("TODO: IUAKT-78");
    }

    private ValidationInput mapToValidationInput(ValidationRequest validationRequest, Validate validate) {
        var existingDrugMedication = Optional.ofNullable(validationRequest.getExistingDrugMedications().orElse(null))
                .map(e -> e.stream()
                        .map(this::mapExistingDrugMedication)
                        .toList());
        return new ValidationInput(
                validationRequest.getAge(),
                validate.getNewDrugMedication().getDrugIdentifier(),
                validate.getNewDrugMedication().getIndicationCode(),
                existingDrugMedication);
    }

    private dk.kvalitetsit.itukt.common.model.ExistingDrugMedication mapExistingDrugMedication(ExistingDrugMedication existing) {
        return new dk.kvalitetsit.itukt.common.model.ExistingDrugMedication(existing.getAtcCode(), existing.getFormCode(), existing.getRouteOfAdministrationCode());
    }
}
