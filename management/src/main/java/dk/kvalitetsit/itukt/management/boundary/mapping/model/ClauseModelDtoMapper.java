package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ClauseModelDtoMapper implements Mapper<Clause, org.openapitools.model.ClauseOutput> {

    private final Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper;

    public ClauseModelDtoMapper(Mapper<Expression, org.openapitools.model.Expression> expressionModelMapper) {
        this.expressionModelMapper = expressionModelMapper;
    }

    @Override
    public org.openapitools.model.ClauseOutput map(Clause entry) {
        var dto = new org.openapitools.model.ClauseOutput(
                entry.name(),
                this.expressionModelMapper.map(entry.expression()),
                null,
                entry.uuid().get()
        );
        entry.uuid().ifPresent(dto::uuid);
        return dto;
    }
}
