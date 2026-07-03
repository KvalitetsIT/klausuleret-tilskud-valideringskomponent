package dk.kvalitetsit.itukt.validation.boundary;


import dk.kvalitetsit.itukt.validation.service.ValidationService;
import org.openapitools.api.ValidationApi;
import org.openapitools.model.ValidationFailed;
import org.openapitools.model.ValidationRequest;
import org.openapitools.model.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@Transactional
public class ValidationController implements ValidationApi {
    private final Logger logger = LoggerFactory.getLogger(ValidationController.class);

    private final ValidationService<ValidationRequest, ValidationResponse> service;

    public ValidationController(@Autowired ValidationService<ValidationRequest, ValidationResponse> service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<ValidationResponse> validation20250801ValidatePost(ValidationRequest validationRequest) {
        var response = service.validate(validationRequest);
        logValidationErrors(response);
        return ResponseEntity.ok(response);
    }

    /**
     * Logs validation errors for statistical purposes.
     */
    private void logValidationErrors(ValidationResponse response) {
        if (response instanceof ValidationFailed validationFailed) {
            var clauseCodes = validationFailed.getValidationErrors().stream()
                    .map(error -> error.getClause().getCode())
                    .collect(Collectors.toSet());
            logger.info("Validation failed for clauses: {}", clauseCodes);
        }
    }
}
