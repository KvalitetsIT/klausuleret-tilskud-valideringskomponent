package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import org.openapitools.model.*;

import java.util.List;
import java.util.Optional;

import static dk.kvalitetsit.itukt.common.model.ValidationInput.*;

/**
 * The {@code ValidationServiceAdaptor} class is responsible for adapting between the boundary layer {@link dk.kvalitetsit.itukt.validation.boundary.ValidationController} and the service layer {@link ValidationServiceImpl}.
 * <p>
 * This class accommodates isolation in terms of testing as mocking mappers will be avoided
 */
public class ValidationServiceAdaptor implements ValidationService<ValidationRequest, ValidationResponse> {

    private final ValidationService<ValidationInput, List<dk.kvalitetsit.itukt.validation.service.model.ValidationError>> validationService;

    public ValidationServiceAdaptor(
            ValidationService<ValidationInput, List<dk.kvalitetsit.itukt.validation.service.model.ValidationError>> validationService
    ) {
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
                .elementPath(validateInput.getElementPath())
                .code(modelValidationError.code())
                .message(modelValidationError.message())
                .clause(new Clause()
                        .message(modelValidationError.clause().message())
                        .code(modelValidationError.clause().code())
                        .text(modelValidationError.clause().text())
                );
    }

    private ValidationInput mapToValidationInput(ValidationRequest validationRequest, Validate validate) {
        var existingDrugMedication = Optional.ofNullable(validationRequest.getExistingDrugMedications().orElse(null))
                .map(e -> e.stream()
                        .map(this::mapExistingDrugMedication)
                        .toList());
        Actor createdBy = validate.getNewDrugMedication().getCreatedBy();
        Optional<Actor> reportedBy = validate.getNewDrugMedication().getReportedBy();
        return new ValidationInput(
                validationRequest.getPersonIdentifier(),
                new CreatedBy(createdBy.getIdentifier(), createdBy.getSpecialityCode()),
                reportedBy.map(actor -> new ReportedBy(actor.getIdentifier(), actor.getSpecialityCode())),
                validationRequest.getSkipValidations(),
                validationRequest.getAge(),
                validate.getNewDrugMedication().getDrugIdentifier(),
                validate.getNewDrugMedication().getIndicationCode(),
                existingDrugMedication);
    }

    private dk.kvalitetsit.itukt.common.model.ExistingDrugMedication mapExistingDrugMedication(ExistingDrugMedicationInput existing) {
        return new dk.kvalitetsit.itukt.common.model.ExistingDrugMedication(existing.getAtcCode(), existing.getFormCode(), existing.getRouteOfAdministrationCode());
    }
}
