package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.DslOutput;


public class ClauseModelDslMapper implements Mapper<Clause, DslOutput> {
    private final Mapper<Expression, String> expressionDslMapper;

    public ClauseModelDslMapper(Mapper<Expression, String> expressionDslMapper) {
        this.expressionDslMapper = expressionDslMapper;
    }

    @Override
    public DslOutput map(Clause entry) {
        return new DslOutput(entry.uuid(), "Klausul " + entry.name() + ": " + this.expressionDslMapper.map(entry.expression()));
    }
}
