package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import org.openapitools.model.*;

import java.util.List;
import java.util.Optional;

/**
 * The {@code ValidationServiceAdaptor} class is responsible for adapting between the boundary layer and the service layer {@link ValidationServiceImpl}.
 * <p>
 * This class accommodates isolation in terms of testing as mocking mappers will be avoided
 */
public class ValidationServiceAdaptor implements ValidationService<ValidationRequest, ValidationResponse> {

    private final ValidationService<ValidationInput, List<dk.kvalitetsit.itukt.validation.service.model.ValidationError>> validationService;

    public ValidationServiceAdaptor(ValidationService<ValidationInput, List<dk.kvalitetsit.itukt.validation.service.model.ValidationError>> validationService) {
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

    private List<ValidationError> validate(ValidationRequest request, Validate validate) {
        var validationInput = mapToValidationInput(request, validate);
        return validationService.validate(validationInput)
                .stream()
                .map(error -> mapValidationError(validate, error))
                .toList();
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
                validationRequest.getPersonIdentifier(),
                validate.getNewDrugMedication().getCreatedBy().getIdentifier(),
                validate.getNewDrugMedication().getReportedBy().map(Actor::getIdentifier),
                validationRequest.getSkipValidations(),
                validationRequest.getAge(),
                validate.getNewDrugMedication().getDrugIdentifier(),
                validate.getNewDrugMedication().getIndicationCode(),
                existingDrugMedication);
    }

    private Condition.ExistingDrugMedication mapExistingDrugMedication(ExistingDrugMedication existing) {
        return new Condition.ExistingDrugMedication(existing.getAtcCode(), existing.getFormCode(), existing.getRouteOfAdministrationCode());
    }
}
