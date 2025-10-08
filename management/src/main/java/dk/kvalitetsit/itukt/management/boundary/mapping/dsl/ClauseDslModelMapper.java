package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.ClauseInput;
import org.openapitools.model.DslInput;


public class ClauseDslModelMapper implements Mapper<DslInput, ClauseInput> {

    @Override
    public ClauseInput map(DslInput dsl) {

        var tokens = new Lexer(dsl.getDsl()).getTokens();
        var parser = new Parser(tokens);

        var name = parser.parseClauseName();
        var expression = parser.parseExpression();

        return new ClauseInput(name, expression, dsl.getError());
    }
}
