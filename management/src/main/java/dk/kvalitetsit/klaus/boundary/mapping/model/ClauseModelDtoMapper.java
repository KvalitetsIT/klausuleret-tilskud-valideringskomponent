package dk.kvalitetsit.klaus.boundary.mapping.model;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.model.Expression;

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
        entry.version().ifPresent(dto::version);
        return dto;
    }
}
