package dk.kvalitetsit.itukt.management.service.model.validation;

public sealed interface ExpressionValidationError permits
        UnknownDepartmentSpecialityError,
        UnknownFormCodeError {
    String errorMessage();
}
