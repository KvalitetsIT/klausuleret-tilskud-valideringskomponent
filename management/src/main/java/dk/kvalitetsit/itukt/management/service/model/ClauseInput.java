package dk.kvalitetsit.itukt.management.service.model;

import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public record ClauseInput(
        String name,
        ExpressionEntity expression,
        String errorMessage
) {

}
