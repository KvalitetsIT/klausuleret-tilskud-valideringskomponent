package dk.kvalitetsit.itukt.management.service.model;

import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public record ClauseForCreation(
        String name,
        ExpressionEntity expression) {
}
