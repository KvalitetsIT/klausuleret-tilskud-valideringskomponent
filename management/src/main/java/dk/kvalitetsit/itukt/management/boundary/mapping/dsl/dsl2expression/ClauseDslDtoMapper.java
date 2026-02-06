package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.DslParser;
import org.openapitools.model.ClauseInput;
import org.openapitools.model.DslInput;


public class ClauseDslDtoMapper implements Mapper<DslInput, ClauseInput> {
    private final DslParser dslParser;

    public ClauseDslDtoMapper(DslParser dslParser) {
        this.dslParser = dslParser;
    }

    @Override
    public ClauseInput map(DslInput dsl) {
        var expression = dslParser.parse(dsl.getDsl());
        return new ClauseInput(dsl.getName(), expression, dsl.getError());
    }
}
