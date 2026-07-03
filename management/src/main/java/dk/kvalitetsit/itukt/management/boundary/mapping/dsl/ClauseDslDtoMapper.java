package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParser;
import dk.kvalitetsit.itukt.management.exceptions.DslParserException;
import org.openapitools.model.ClauseInput;
import org.openapitools.model.DslInput;

public class ClauseDslDtoMapper {
    private final DslParser dslParser;

    public ClauseDslDtoMapper(DslParser dslParser) {
        this.dslParser = dslParser;
    }

    public ClauseInput map(DslInput dsl) throws DslParserException {
        var expression = dslParser.parse(dsl.getDsl());
        return new ClauseInput(dsl.getName(), expression, dsl.getError());
    }
}
