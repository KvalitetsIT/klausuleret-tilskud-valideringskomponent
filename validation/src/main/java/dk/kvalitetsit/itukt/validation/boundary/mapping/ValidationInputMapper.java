package dk.kvalitetsit.itukt.validation.boundary.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
import org.openapitools.model.Validate;
import org.openapitools.model.ValidationRequest;

import java.util.List;

public class ValidationInputMapper implements Mapper<ValidationRequest, List<ValidationInput>> {
    @Override
    public List<ValidationInput> map(ValidationRequest validationRequest) {
        return validationRequest.getValidate().stream()
                .map(validate -> validateItemToValidationInput(validationRequest, validate))
                .toList();
    }

    private ValidationInput validateItemToValidationInput(ValidationRequest validationRequest, Validate validate) {
        return new ValidationInput(
                validationRequest.getAge(),
                validate.getNewDrugMedication().getDrugIdentifier());
    }
}
