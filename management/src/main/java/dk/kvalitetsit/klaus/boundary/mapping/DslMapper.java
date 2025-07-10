package dk.kvalitetsit.klaus.boundary.mapping;

import dk.kvalitetsit.klaus.Mapper;
import org.openapitools.model.Expression;


public class DslMapper implements Mapper<String, Expression> {

    @Override
    public Expression map(String dsl) {
        var tokens = new Lexer(dsl).getTokens();
        return new Parser(tokens).parseClause();
    }
}
