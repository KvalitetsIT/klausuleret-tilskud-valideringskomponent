package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.ConditionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder.ConditionBuilder;

import java.util.List;

public class TokenParserFactory {
    private final ConditionTokenParser conditionTokenParser;
    private final List<ConditionBuilder> conditionBuilders;

    public TokenParserFactory(ConditionTokenParser conditionTokenParser, List<ConditionBuilder> conditionBuilders) {
        this.conditionTokenParser = conditionTokenParser;
        this.conditionBuilders = conditionBuilders;
    }

    public BinaryExpressionTokenParser createBinaryExpressionTokenParser(ExpressionTokenParser expressionTokenParser) {
        var parenthesizedExpressionTokenParser = createParenthesizedExpressionTokenParser(expressionTokenParser);
        var conditionExpressionTokenParser = createConditionExpressionTokenParser();
        return new BinaryExpressionTokenParser(parenthesizedExpressionTokenParser, conditionExpressionTokenParser);
    }

    public ParenthesizedExpressionTokenParser createParenthesizedExpressionTokenParser(ExpressionTokenParser expressionTokenParser) {
        return new ParenthesizedExpressionTokenParser(expressionTokenParser);
    }

    public ConditionExpressionTokenParser createConditionExpressionTokenParser() {
        return new ConditionExpressionTokenParser(conditionTokenParser, conditionBuilders);
    }
}
