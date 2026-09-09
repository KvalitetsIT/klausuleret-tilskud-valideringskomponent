package dk.kvalitetsit.itukt.management.service.model.validation;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;

public record UnknownValueError(
        Identifier identifier,
        String value
) implements ExpressionValidationError {

    @Override
    public String errorMessage() {
        return "Unknown %s '%s'.".formatted(
                identifier.name().toLowerCase(),
                value);
    }
}
