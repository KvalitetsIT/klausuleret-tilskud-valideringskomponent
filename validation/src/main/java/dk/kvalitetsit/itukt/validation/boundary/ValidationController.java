package dk.kvalitetsit.itukt.validation.boundary;


import dk.kvalitetsit.itukt.validation.service.ValidationService;
import org.openapitools.api.ValidationApi;
import org.openapitools.model.ValidationRequest;
import org.openapitools.model.ValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController implements ValidationApi {

    private final ValidationService<ValidationRequest, ValidationResponse> service;

    public ValidationController(@Autowired ValidationService<ValidationRequest, ValidationResponse> service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<ValidationResponse> call20250801validatePost(ValidationRequest validationRequest) {
        var response = service.validate(validationRequest);
        return ResponseEntity.ok(response);
    }
}
