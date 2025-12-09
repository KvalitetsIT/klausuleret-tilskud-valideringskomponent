package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.repository.cache.Cache;
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
    private final Cache<Department.Identifier, Department> departmentCache;

    public ValidationServiceAdaptor(
            Cache<Department.Identifier, Department> departmentCache,
            ValidationService<ValidationInput, List<dk.kvalitetsit.itukt.validation.service.model.ValidationError>> validationService
    ) {
        this.validationService = validationService;
        this.departmentCache = departmentCache;
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
                        .toList()
                );

        var createdBy = getSpecialities(validate.getNewDrugMedication().getCreatedBy());
        var reportedBy = validate.getNewDrugMedication().getReportedBy().map(this::getSpecialities);

        return new ValidationInput(
                validationRequest.getPersonIdentifier(),
                createdBy,
                reportedBy,
                validationRequest.getSkipValidations(),
                validationRequest.getAge(),
                validate.getNewDrugMedication().getDrugIdentifier(),
                validate.getNewDrugMedication().getIndicationCode(),
                null,
                existingDrugMedication
        );
    }

    private ValidationInput.Actor getSpecialities(Actor actor) {
        var department = actor.getDepartmentIdentifier().flatMap(x -> {
            var id = switch (x.getType()) {
                case SOR -> new Department.Identifier.SOR(x.getCode());
                case SHAK -> new Department.Identifier.SHAK(x.getCode());
            };

            return departmentCache.get(id);
        });

        return new ValidationInput.Actor(actor.getIdentifier(), department);
    }

    private dk.kvalitetsit.itukt.common.model.ExistingDrugMedication mapExistingDrugMedication(ExistingDrugMedicationInput existing) {
        return new dk.kvalitetsit.itukt.common.model.ExistingDrugMedication(existing.getAtcCode(), existing.getFormCode(), existing.getRouteOfAdministrationCode());
    }
}
