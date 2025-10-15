package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.DslOutput;
import org.openapitools.model.Error;

public class ClauseModelDslMapper implements Mapper<Clause.Persisted, DslOutput> {
    private final Mapper<Expression.Persisted, String> expressionDslMapper;
    private final Mapper<dk.kvalitetsit.itukt.common.model.Error, Error> errorMapper;

    public ClauseModelDslMapper(Mapper<Expression.Persisted, String> expressionDslMapper, Mapper<dk.kvalitetsit.itukt.common.model.Error, Error> errorMapper) {
        this.expressionDslMapper = expressionDslMapper;
        this.errorMapper = errorMapper;
    }

    @Override
    public DslOutput map(Clause.Persisted entry) {
        return new DslOutput(
                errorMapper.map(entry.error()),
                "Klausul " + entry.name() + ": " + this.expressionDslMapper.map(entry.expression()),
                entry.uuid()
        );
    }
}
