package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.Clause;


public class DslClauseMapper implements Mapper<String, Clause> {

    @Override
    public Clause map(String dsl) {
        var tokens = new Lexer(dsl).getTokens();
        return new Parser(tokens).parseClause();
    }
}
