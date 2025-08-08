package dk.kvalitetsit.itukt.validation.boundary;


import dk.kvalitetsit.itukt.validation.service.ValidationService;
import org.openapitools.api.ValidationApi;
import org.openapitools.model.ValidationRequest;
import org.openapitools.model.ValidationResponse;
import org.openapitools.model.ValidationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController implements ValidationApi {

    private final ValidationService<ValidationRequest> service;

    public ValidationController(@Autowired ValidationService<ValidationRequest> service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<ValidationResponse> call20250801validatePost(ValidationRequest validationRequest) {
        var validateSuccess = service.validate(validationRequest);
        var validationStatus = validateSuccess
                ? ValidationStatus.VALIDATED
                : ValidationStatus.VALIDATION_FAILED;
        return ResponseEntity.ok(new ValidationResponse().validationStatus(validationStatus));
    }
}
