package dk.kvalitetsit.itukt.boundary.mapping.model;

import dk.kvalitetsit.itukt.Mapper;
import dk.kvalitetsit.itukt.model.Clause;
import dk.kvalitetsit.itukt.model.Expression;

public class ClauseModelDtoMapper implements Mapper<Clause, org.openapitools.model.Clause> {

    private final Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper;

    public ClauseModelDtoMapper(Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper) {
        this.expressionModelMapper = expressionModelMapper;
    }

    @Override
    public org.openapitools.model.Clause map(Clause entry) {
        var dto = new org.openapitools.model.Clause(
                entry.name(),
                this.expressionModelMapper.map(entry.expression()));
        entry.uuid().ifPresent(dto::uuid);
        return dto;
    }
}
