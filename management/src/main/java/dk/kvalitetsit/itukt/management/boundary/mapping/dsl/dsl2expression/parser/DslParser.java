package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.Lexer;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.Token;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.expression.ExpressionTokenParser;
import org.openapitools.model.Expression;

import java.util.List;

public class DslParser {
    private final ExpressionTokenParser expressionTokenParser;
    private final Lexer lexer;

    public DslParser(ExpressionTokenParser expressionTokenParser, Lexer lexer) {
        this.expressionTokenParser = expressionTokenParser;
        this.lexer = lexer;
    }

    public Expression parse(String dsl) {
        List<Token> tokens = lexer.getTokens(dsl);
        var tokenIterator = new TokenIterator(tokens);
        Expression expression = expressionTokenParser.parse(tokenIterator);
        tokenIterator.expectNoMoreTokens();
        return expression;
    }
}
