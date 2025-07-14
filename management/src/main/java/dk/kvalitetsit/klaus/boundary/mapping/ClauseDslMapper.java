package dk.kvalitetsit.klaus.boundary.mapping;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.model.Expression;


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
