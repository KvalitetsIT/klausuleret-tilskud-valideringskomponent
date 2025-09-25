package dk.kvalitetsit.itukt.common.model;


import dk.kvalitetsit.itukt.common.entity.ClauseEntity;

public record Clause(
        String name,
        Expression expression
) implements ClauseEntity { }
