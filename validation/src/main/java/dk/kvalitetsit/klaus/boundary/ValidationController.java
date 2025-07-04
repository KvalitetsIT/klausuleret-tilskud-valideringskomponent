package dk.kvalitetsit.klaus.boundary;


import dk.kvalitetsit.klaus.service.ValidationService;
import org.openapitools.api.ValidationApi;
import org.openapitools.model.Prescription;
import org.openapitools.model.ValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController implements ValidationApi {

    private final ValidationService<Prescription> service;

    public ValidationController(@Autowired ValidationService<Prescription> service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<Boolean> v1ValidationsPost(ValidationRequest validationRequest) {
        var all_prescriptions_are_valid = validationRequest.getPrescriptions().stream().allMatch(prescription -> service.validate(prescription));
        return ResponseEntity.ok(all_prescriptions_are_valid);
    }
}
