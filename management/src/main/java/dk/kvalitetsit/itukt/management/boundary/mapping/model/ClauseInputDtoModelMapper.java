package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.ClauseInput;

public class ClauseInputDtoModelMapper implements Mapper<ClauseInput, Clause.NotPersisted> {
    private final Mapper<org.openapitools.model.Expression, Expression.NotPersisted> mapper;

    public ClauseInputDtoModelMapper(Mapper<org.openapitools.model.Expression, Expression.NotPersisted> mapper) {
        this.mapper = mapper;
    }

    @Override
    public  Clause.NotPersisted map(ClauseInput clauseInput) {
        var expression = mapper.map(clauseInput.getExpression());
        return new  Clause.NotPersisted(clauseInput.getName(), expression, clauseInput.getError().getMessage());
    }
}
