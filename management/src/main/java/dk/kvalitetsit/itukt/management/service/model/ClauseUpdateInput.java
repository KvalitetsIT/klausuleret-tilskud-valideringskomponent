package dk.kvalitetsit.itukt.management.service.model;

import dk.kvalitetsit.itukt.common.model.Expression;

public record ClauseUpdateInput(
        Expression expression,
        String errorMessage
) {

}
