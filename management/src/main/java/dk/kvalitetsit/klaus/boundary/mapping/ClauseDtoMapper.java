package dk.kvalitetsit.klaus.boundary.mapping;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.model.Expression;

public class ClauseDtoMapper implements Mapper<Clause, org.openapitools.model.Clause>{

    private  final Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper;

    public ClauseDtoMapper(Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper) {
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
