package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.UnexpectedEmptyMultiValueConditionException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.UnexpectedValueException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.ConditionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder.ConditionBuilder;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.BinaryOperator;
import org.openapitools.model.Expression;
import org.openapitools.model.Operator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Token parser that handles condition expressions, consisting of an identifier, an operator, and one or more values.
 * E.g. "alder > 30" or "indikation i [A, B, C]"
 */
public class ConditionExpressionTokenParser implements TokenParser<Expression> {
    private final ConditionTokenParser conditionTokenParser;
    private final Map<Identifier, ConditionBuilder> conditionBuilders;

    public ConditionExpressionTokenParser(ConditionTokenParser conditionTokenParser, List<ConditionBuilder> conditionBuilders) {
        this.conditionTokenParser = conditionTokenParser;
        this.conditionBuilders = conditionBuilders.stream()
                .collect(Collectors.toMap(ConditionBuilder::identifier, Function.identity()));
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return conditionTokenParser.canParse(tokens);
    }

    @Override
    public Expression parse(TokenIterator tokens) throws DslParserException {
        Condition condition = conditionTokenParser.parse(tokens);
        var conditionBuilder = Optional.ofNullable(conditionBuilders.get(condition.identifier()))
                .orElseThrow(() -> new UnexpectedValueException(condition.identifier().toString()));
        return buildExpression(condition, conditionBuilder);
    }

    private Expression buildExpression(Condition condition, ConditionBuilder conditionBuilder) throws DslParserException {
        return switch (condition) {
            case Condition.SingleValueCondition svc -> conditionBuilder.build(svc.operator(), svc.value());
            case Condition.MultiValueCondition mvc -> buildMultiValueConditionExpression(mvc, conditionBuilder);
        };
    }

    private Expression buildMultiValueConditionExpression(Condition.MultiValueCondition condition, ConditionBuilder conditionBuilder) throws DslParserException {
        var values = condition.values().iterator();
        if (!values.hasNext()) {
            throw new UnexpectedEmptyMultiValueConditionException();
        }

        Expression expression = conditionBuilder.build(Operator.EQUAL, values.next());
        while (values.hasNext()) {
            Expression nextExpression = conditionBuilder.build(Operator.EQUAL, values.next());
            expression = new BinaryExpression(expression, BinaryOperator.OR, nextExpression, ExpressionType.BINARY);
        }
        return expression;
    }
}
