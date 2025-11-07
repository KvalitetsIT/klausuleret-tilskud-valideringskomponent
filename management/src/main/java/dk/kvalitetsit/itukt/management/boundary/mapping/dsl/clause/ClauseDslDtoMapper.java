package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.ClauseInput;
import org.openapitools.model.DslInput;


public class ClauseDslDtoMapper implements Mapper<DslInput, ClauseInput> {

    @Override
    public ClauseInput map(DslInput dsl) {

        var tokens = new Lexer(dsl.getDsl()).getTokens();
        var parser = new Parser(tokens);

        var parsedClause = parser.parseClause();

        return new ClauseInput(parsedClause.name(), parsedClause.expression(), dsl.getError());
    }
}
