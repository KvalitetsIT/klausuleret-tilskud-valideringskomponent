package dk.kvalitetsit.klaus.boundary;


import dk.kvalitetsit.klaus.service.ValidationService;
import org.openapitools.api.ValidationApi;
import org.openapitools.model.ValidationRequest;
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
    public ResponseEntity<Boolean> call20250801validationsPost(ValidationRequest validationRequest) {
        var all_prescriptions_are_valid = service.validate(validationRequest);
        return ResponseEntity.ok(all_prescriptions_are_valid);
    }
}
