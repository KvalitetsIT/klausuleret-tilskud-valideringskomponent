package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenIterator;
import org.openapitools.model.Expression;

public class ParenthesizedExpressionTokenParser implements TokenParser<Expression> {
    private final ExpressionTokenParser expressionTokenParser;

    public ParenthesizedExpressionTokenParser(ExpressionTokenParser expressionTokenParser) {
        this.expressionTokenParser = expressionTokenParser;
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return tokens.nextHasText("(");
    }

    @Override
    public Expression parse(TokenIterator tokens) {
        tokens.nextWithText("(");
        var expression = expressionTokenParser.parse(tokens);
        tokens.nextWithText(")");
        return expression;
    }
}
