package dk.kvalitetsit.itukt.boundary.mapping.dto;

import dk.kvalitetsit.itukt.Mapper;
import dk.kvalitetsit.itukt.model.Expression;
import org.openapitools.model.Clause;

public class DtoClauseMapper implements Mapper<Clause, dk.kvalitetsit.itukt.model.Clause> {
    private final Mapper<org.openapitools.model.Expression, Expression> expressionMapper;

    public DtoClauseMapper(Mapper<org.openapitools.model.Expression, Expression> expressionMapper) {
        this.expressionMapper = expressionMapper;
    }

    @Override
    public dk.kvalitetsit.itukt.model.Clause map(Clause entry) {
        return new dk.kvalitetsit.itukt.model.Clause(entry.getName(), entry.getUuid(), this.expressionMapper.map(entry.getExpression()));
    }
}
