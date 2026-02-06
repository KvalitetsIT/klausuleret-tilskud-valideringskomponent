package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.expression;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.TokenIterator;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.BinaryOperator;
import org.openapitools.model.Expression;

public class BinaryExpressionTokenParser implements TokenParser<Expression> {
    private static final String OR_KEYWORD = "eller";
    private static final String AND_KEYWORD = "og";
    private final ParenthesizedExpressionTokenParser parenthesizedParser;
    private final ConditionExpressionTokenParser conditionParser;

    public BinaryExpressionTokenParser(ParenthesizedExpressionTokenParser parenthesizedExpressionTokenParser, ConditionExpressionTokenParser conditionExpressionTokenParser) {
        this.parenthesizedParser = parenthesizedExpressionTokenParser;
        this.conditionParser = conditionExpressionTokenParser;
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return parenthesizedParser.canParse(tokens) || conditionParser.canParse(tokens);
    }

    @Override
    public Expression parse(TokenIterator tokens) {
        return parseOr(tokens);
    }

    private Expression parseOr(TokenIterator tokens) {
        Expression expression = parseAnd(tokens);
        while (tokens.nextHasText(OR_KEYWORD)) {
            tokens.nextWithText(OR_KEYWORD);
            Expression right = parseAnd(tokens);
            expression = new BinaryExpression(expression, BinaryOperator.OR, right, ExpressionType.BINARY);
        }
        return expression;
    }

    private Expression parseAnd(TokenIterator tokens) {
        Expression expression = parseAndOperand(tokens);
        while (tokens.nextHasText(AND_KEYWORD)) {
            tokens.nextWithText(AND_KEYWORD);
            Expression right = parseAndOperand(tokens);
            expression = new BinaryExpression(expression, BinaryOperator.AND, right, ExpressionType.BINARY);
        }
        return expression;
    }

    private Expression parseAndOperand(TokenIterator tokens) {
        if (parenthesizedParser.canParse(tokens)) {
            return parenthesizedParser.parse(tokens);
        } else {
            return conditionParser.parse(tokens);
        }
    }
}
