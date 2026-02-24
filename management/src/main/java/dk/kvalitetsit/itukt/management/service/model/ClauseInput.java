package dk.kvalitetsit.itukt.management.service.model;

import dk.kvalitetsit.itukt.common.model.Expression;

public record ClauseInput(
        String name,
        Expression expression,
        String errorMessage
) {

}
