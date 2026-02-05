package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Lexer;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Token;
import org.openapitools.model.Expression;

import java.util.List;

public class DslParser {
    private final ExpressionTokenParser expressionTokenParser;

    public DslParser(ExpressionTokenParser expressionTokenParser) {
        this.expressionTokenParser = expressionTokenParser;
    }

    public Expression parse(String dsl) {
        List<Token> tokens = new Lexer(dsl).getTokens();
        var tokenIterator = new TokenIterator(tokens);
        Expression expression = expressionTokenParser.parse(tokenIterator);
        tokenIterator.expectNoMoreTokens();
        return expression;
    }
}
