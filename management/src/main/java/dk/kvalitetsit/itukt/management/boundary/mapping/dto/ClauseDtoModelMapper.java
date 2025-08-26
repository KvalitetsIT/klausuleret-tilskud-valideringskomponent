package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.Clause;

public class ClauseDtoModelMapper implements Mapper<Clause, dk.kvalitetsit.itukt.common.model.Clause> {
    private final Mapper<org.openapitools.model.Expression, Expression> expressionMapper;

    public ClauseDtoModelMapper(Mapper<org.openapitools.model.Expression, Expression> expressionMapper) {
        this.expressionMapper = expressionMapper;
    }

    @Override
    public dk.kvalitetsit.itukt.common.model.Clause map(Clause entry) {
        return new dk.kvalitetsit.itukt.common.model.Clause(entry.getName(), entry.getUuid(), this.expressionMapper.map(entry.getExpression()));
    }
}
