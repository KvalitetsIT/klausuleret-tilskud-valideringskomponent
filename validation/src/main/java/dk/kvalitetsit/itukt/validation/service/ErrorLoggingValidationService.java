package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Logs validation errors for statistical purposes.
 */
public class ErrorLoggingValidationService implements ValidationService<ValidationInput, List<ValidationError>> {
    private final Logger logger = LoggerFactory.getLogger(ErrorLoggingValidationService.class);
    private final ValidationService<ValidationInput, List<ValidationError>> validationService;

    public ErrorLoggingValidationService(ValidationService<ValidationInput, List<ValidationError>> validationService) {
        this.validationService = validationService;
    }

    @Override
    public List<ValidationError> validate(ValidationInput prescription) {
        var validationErrors = validationService.validate(prescription);
        logErrors(validationErrors);
        return validationErrors;
    }

    private void logErrors(List<ValidationError> validationErrors) {
        if (!validationErrors.isEmpty()) {
            var clauseCodes = validationErrors.stream()
                    .map(error -> error.clause().code())
                    .collect(Collectors.toSet());
            logger.info("Validation failed for clauses: {}", clauseCodes);
        }
    }
}
