package dk.kvalitetsit.itukt.management.exceptions;

import dk.kvalitetsit.itukt.management.service.model.validation.ExpressionValidationError;

import java.util.List;

public final class RequiresForceException extends ManagementException {

    private final List<ExpressionValidationError> validationErrors;

    public RequiresForceException(List<ExpressionValidationError> validationErrors) {
        super("Requires force. Validation errors: " + validationErrors.stream().map(ExpressionValidationError::errorMessage).toList());
        this.validationErrors = validationErrors;
    }

    public List<ExpressionValidationError> getValidationErrors() {
        return validationErrors;
    }
}
