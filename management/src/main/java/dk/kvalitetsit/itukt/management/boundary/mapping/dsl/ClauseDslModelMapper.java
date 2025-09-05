package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.ClauseInput;


public class ClauseDslModelMapper implements Mapper<String, ClauseInput> {

    @Override
    public ClauseInput map(String dsl) {
        var tokens = new Lexer(dsl).getTokens();
        return new Parser(tokens).parseClause();
    }
}
