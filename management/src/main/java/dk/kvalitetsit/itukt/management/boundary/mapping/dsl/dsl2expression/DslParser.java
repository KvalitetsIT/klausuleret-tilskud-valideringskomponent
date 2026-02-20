package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.ExpressionTokenParser;
import org.openapitools.model.Expression;

public class DslParser {
    private final ExpressionTokenParser expressionTokenParser;
    private final Lexer lexer;

    public DslParser(ExpressionTokenParser expressionTokenParser, Lexer lexer) {
        this.expressionTokenParser = expressionTokenParser;
        this.lexer = lexer;
    }

    public Expression parse(String dsl) {
        var tokenIterator = lexer.getTokens(dsl);
        Expression expression = expressionTokenParser.parse(tokenIterator);
        tokenIterator.expectNoMoreTokens();
        return expression;
    }
}
