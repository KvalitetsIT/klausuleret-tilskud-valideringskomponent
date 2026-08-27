package dk.kvalitetsit.itukt.management.service.model.validation;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;

import java.util.Set;

public record UnknownValueError(
        Identifier identifier,
        String value,
        Set<String> knownValues
) implements ExpressionValidationError {

    @Override
    public String errorMessage() {
        return "Unknown %s '%s'. Known values are: %s".formatted(
                identifier.name().toLowerCase(),
                value,
                knownValues.stream().sorted().toList());
    }
}
