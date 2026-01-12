package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import org.openapitools.model.*;

import java.util.List;

/**
 * The {@code ValidationServiceAdaptor} class is responsible for adapting between the boundary layer {@link dk.kvalitetsit.itukt.validation.boundary.ValidationController} and the service layer {@link ValidationServiceImpl}.
 * <p>
 * This class accommodates isolation in terms of testing as mocking mappers will be avoided
 */
public class ValidationServiceAdaptor implements ValidationService<ValidationRequest, ValidationResponse> {

    private final ValidationService<ValidationInput, List<dk.kvalitetsit.itukt.validation.service.model.ValidationError>> validationService;
    private final Mapper<ValidationRequest, List<ValidationInput>> validationRequestInputMapper;
    private final Mapper<dk.kvalitetsit.itukt.validation.service.model.ValidationError, ValidationError> errorMapper;

    public ValidationServiceAdaptor(
            ValidationService<ValidationInput, List<dk.kvalitetsit.itukt.validation.service.model.ValidationError>> validationService,
            Mapper<ValidationRequest, List<ValidationInput>> validationRequestInputMapper,
            Mapper<dk.kvalitetsit.itukt.validation.service.model.ValidationError, ValidationError> errorMapper
    ) {
        this.validationService = validationService;
        this.validationRequestInputMapper = validationRequestInputMapper;
        this.errorMapper = errorMapper;
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
        List<ValidationInput> validationInputs = this.validationRequestInputMapper.map(request);

        var validationErrors = validationInputs.stream()
                .flatMap(input -> validate(input).stream())
                .toList();

        return validationErrors.isEmpty() ?
                new ValidationSuccess() :
                new ValidationFailed().validationErrors(validationErrors);
    }

    private List<ValidationError> validate(ValidationInput input) {
        return validationService.validate(input)
                .stream()
                .map(errorMapper::map)
                .toList();
    }
}
