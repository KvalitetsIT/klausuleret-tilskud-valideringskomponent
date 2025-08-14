package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationResult;
import org.openapitools.model.ValidationRequest;
import org.openapitools.model.ValidationResponse;
import org.openapitools.model.ValidationSuccess;

import java.util.List;

/**
 * The {@code ValidationServiceAdaptor} class is responsible for adapting between the boundary layer and the service layer {@link ValidationServiceImpl}.
 * <p>
 * This class accommodates isolation in terms of testing as mocking mappers will be avoided
 */
public class ValidationServiceAdaptor implements ValidationService<ValidationRequest, ValidationResponse> {

    private final ValidationService<ValidationInput, ValidationResult> service;
    private final Mapper<ValidationRequest, List<ValidationInput>> mapper;

    public ValidationServiceAdaptor(ValidationService<ValidationInput, ValidationResult> service, Mapper<ValidationRequest, List<ValidationInput>> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public ValidationResponse validate(ValidationRequest request) {
        return new ValidationSuccess();
    }
}
