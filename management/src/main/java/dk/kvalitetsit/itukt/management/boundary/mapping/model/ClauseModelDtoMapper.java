package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.Error;

public class ClauseModelDtoMapper implements Mapper<Clause, org.openapitools.model.ClauseOutput> {

    private final Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper;
    private final Mapper<Clause.Error, Error> errorMapper;

    public ClauseModelDtoMapper(Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper, Mapper<Clause.Error, Error> errorMapper) {
        this.expressionModelMapper = expressionModelMapper;
        this.errorMapper = errorMapper;
    }

    @Override
    public org.openapitools.model.ClauseOutput map(Clause entry) {
        return new org.openapitools.model.ClauseOutput(
                entry.name(),
                this.expressionModelMapper.map(entry.expression()),
                errorMapper.map(entry.error()),
                entry.uuid()
        );
    }
}
