package dk.kvalitetsit.itukt.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.Mapper;
import dk.kvalitetsit.itukt.model.Clause;
import dk.kvalitetsit.itukt.model.Expression;


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
