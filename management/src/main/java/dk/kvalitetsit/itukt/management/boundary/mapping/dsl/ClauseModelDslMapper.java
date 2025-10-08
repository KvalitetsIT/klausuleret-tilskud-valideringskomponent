package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.DslOutput;
import org.openapitools.model.Error;

public class ClauseModelDslMapper implements Mapper<Clause, DslOutput> {
    private final Mapper<Expression, String> expressionDslMapper;
    private final Mapper<Clause.Error, Error> errorMapper;

    public ClauseModelDslMapper(Mapper<Expression, String> expressionDslMapper,  Mapper<Clause.Error, Error> errorMapper) {
        this.expressionDslMapper = expressionDslMapper;
        this.errorMapper = errorMapper;
    }

    @Override
    public DslOutput map(Clause entry) {
        return new DslOutput(
                errorMapper.map(entry.error()),
                "Klausul " + entry.name() + ": " + this.expressionDslMapper.map(entry.expression()),
                entry.uuid()
        );
    }
}
