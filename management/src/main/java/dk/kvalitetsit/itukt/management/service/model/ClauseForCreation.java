package dk.kvalitetsit.itukt.management.service.model;

import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public record ClauseForCreation(
        String name,
        ExpressionEntity expression,
        String errorMessage
) {
    public ClauseForCreation {
        if (expression == null) throw new IllegalArgumentException("Expected an expression but was null");
        if (name == null) throw new IllegalArgumentException("Expected a name but was null");
    }
}
