package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.ClauseInput;

import java.util.Optional;

public class ClauseDtoModelMapper implements Mapper<ClauseInput, dk.kvalitetsit.itukt.common.model.Clause> {
    private final Mapper<org.openapitools.model.Expression, Expression> expressionMapper;

    public ClauseDtoModelMapper(Mapper<org.openapitools.model.Expression, Expression> expressionMapper) {
        this.expressionMapper = expressionMapper;
    }

    @Override
    public dk.kvalitetsit.itukt.common.model.Clause map(ClauseInput entry) {
        return new dk.kvalitetsit.itukt.common.model.Clause(entry.getName(), Optional.empty(), this.expressionMapper.map(entry.getExpression()));
    }
}
