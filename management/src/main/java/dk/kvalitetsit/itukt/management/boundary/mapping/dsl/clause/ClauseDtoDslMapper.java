package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.ClauseOutput;
import org.openapitools.model.DslOutput;

public class ClauseDtoDslMapper implements Mapper<ClauseOutput, DslOutput> {
    private final Mapper<org.openapitools.model.Expression, String> expressionDslMapper;

    public ClauseDtoDslMapper(Mapper<org.openapitools.model.Expression, String> expressionDslMapper) {
        this.expressionDslMapper = expressionDslMapper;
    }

    @Override
    public DslOutput map(ClauseOutput entry) {
        return new DslOutput(
                entry.getError(),
                "Klausul " + entry.getName() + ": " + this.expressionDslMapper.map(entry.getExpression()),
                entry.getUuid()
        );
    }
}
