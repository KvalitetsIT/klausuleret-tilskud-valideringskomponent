package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;

public class ClauseModelDtoMapper implements Mapper<Clause, org.openapitools.model.ClauseOutput> {

    private final Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper;

    public ClauseModelDtoMapper(Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper) {
        this.expressionModelMapper = expressionModelMapper;
    }

    @Override
    public org.openapitools.model.ClauseOutput map(Clause entry) {
        return new org.openapitools.model.ClauseOutput(
                entry.name(),
                this.expressionModelMapper.map(entry.expression()),
                entry.uuid()
        );
    }
}
