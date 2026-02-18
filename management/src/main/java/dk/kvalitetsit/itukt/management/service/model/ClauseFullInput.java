package dk.kvalitetsit.itukt.management.service.model;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;

import java.util.Date;

public record ClauseFullInput(
        String name,
        Expression expression,
        String errorMessage,
        Clause.Status status,
        Date validFrom
) {

}
