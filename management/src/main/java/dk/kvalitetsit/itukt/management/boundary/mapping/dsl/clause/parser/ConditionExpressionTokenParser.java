package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.Condition;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.ConditionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.builder.ConditionBuilder;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.BinaryOperator;
import org.openapitools.model.Expression;
import org.openapitools.model.Operator;

import java.util.List;

public class ConditionExpressionTokenParser implements TokenParser<Expression> {
    private final ConditionTokenParser conditionTokenParser;
    private final List<ConditionBuilder> conditionBuilders;

    public ConditionExpressionTokenParser(ConditionTokenParser conditionTokenParser, List<ConditionBuilder> conditionBuilders) {
        this.conditionTokenParser = conditionTokenParser;
        this.conditionBuilders = conditionBuilders;
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return conditionTokenParser.canParse(tokens);
    }

    @Override
    public Expression parse(TokenIterator tokens) {
        Condition condition = conditionTokenParser.parse(tokens);
        var conditionBuilder = conditionBuilders.stream()
                .filter(builder -> builder.identifier().equals(condition.identifier()))
                .findFirst()
                .orElseThrow(() -> new DslParserException("Unsupported identifier: " + condition.identifier()));
        return buildExpression(condition, conditionBuilder);
    }

    private Expression buildExpression(Condition condition, ConditionBuilder conditionBuilder) {
        return switch (condition) {
            case Condition.SingleValueCondition svc -> conditionBuilder.build(svc.operator(), svc.value());
            case Condition.MultiValueCondition mvc -> buildMultiValueConditionExpression(mvc, conditionBuilder);
        };
    }

    private Expression buildMultiValueConditionExpression(Condition.MultiValueCondition condition, ConditionBuilder conditionBuilder) {
        var conditionExpressions = condition.values().stream()
                .map(value -> conditionBuilder.build(Operator.EQUAL, value)).iterator();
        if (!conditionExpressions.hasNext()) {
            throw new DslParserException("No values provided for multi-value condition");
        }

        Expression expression = conditionExpressions.next();
        while (conditionExpressions.hasNext()) {
            expression = new BinaryExpression(expression, BinaryOperator.OR, conditionExpressions.next(), ExpressionType.BINARY);
        }
        return expression;
    }
}
