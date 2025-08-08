package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;


public class ClauseDslMapper implements Mapper<Clause, String> {
    private final Mapper<Expression, String> expressionDslMapper;

    public ClauseDslMapper(Mapper<Expression, String> expressionDslMapper) {
        this.expressionDslMapper = expressionDslMapper;
    }

    @Override
    public String map(Clause entry) {
        return "Klausul " + entry.name() + ": " + this.expressionDslMapper.map(entry.expression());
    }
}
