package dk.kvalitetsit.klaus.boundary.mapping;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.model.Expression;

public class ClauseModelMapper implements Mapper<Clause, org.openapitools.model.Clause>{

    private  final Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper;

    public ClauseModelMapper(Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper) {
        this.expressionModelMapper = expressionModelMapper;
    }

    @Override
    public org.openapitools.model.Clause map(Clause entry) {
        return new org.openapitools.model.Clause(entry.name(), this.expressionModelMapper.map(entry.expression()));
    }
}
