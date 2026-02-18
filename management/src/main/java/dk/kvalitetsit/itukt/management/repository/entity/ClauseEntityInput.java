package dk.kvalitetsit.itukt.management.repository.entity;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.Date;

public record ClauseEntityInput(
        String name,
        ExpressionEntity expression,
        String errorMessage,
        Clause.Status status,
        Date validFrom
) {

}
