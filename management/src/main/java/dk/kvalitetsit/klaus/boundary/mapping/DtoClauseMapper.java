package dk.kvalitetsit.klaus.boundary.mapping;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Expression;
import org.openapitools.model.Clause;

public class DtoClauseMapper implements Mapper<Clause, dk.kvalitetsit.klaus.model.Clause> {
    private final Mapper<org.openapitools.model.Expression, Expression> expressionMapper;

    public DtoClauseMapper(Mapper<org.openapitools.model.Expression, Expression> expressionMapper) {
        this.expressionMapper = expressionMapper;
    }

    @Override
    public dk.kvalitetsit.klaus.model.Clause map(Clause entry) {
        return new dk.kvalitetsit.klaus.model.Clause(entry.getName(), entry.getUuid(), this.expressionMapper.map(entry.getExpression()));
    }
}
