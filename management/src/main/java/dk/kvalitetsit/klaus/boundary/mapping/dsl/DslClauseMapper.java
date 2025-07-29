package dk.kvalitetsit.klaus.boundary.mapping.dsl;

import dk.kvalitetsit.klaus.Mapper;
import org.openapitools.model.Clause;


public class DslClauseMapper implements Mapper<String, Clause> {

    @Override
    public Clause map(String dsl) {
        var tokens = new Lexer(dsl).getTokens();
        return new Parser(tokens).parseClause();
    }
}
