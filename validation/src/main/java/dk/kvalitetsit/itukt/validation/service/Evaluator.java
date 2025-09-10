package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;

import java.util.Optional;

/**
 * The {@code Evaluator} class is responsible for evaluating logical expressions
 * against a given {@link DataContext}. It supports various types of expressions
 * including conditions, binary expressions (like "og" / "eller"), and parenthesized expressions.
 * <p>
 * This class is typically used in a rule engine or query language parser to evaluate
 * whether a set of data satisfies a logical expression tree.
 */
public class Evaluator {

    /**
     * Evaluates an expression against the provided data context.
     *
     * @param expr the expression to evaluate
     * @param ctx  the context containing data values for fields
     * @return {@code true} if the expression evaluates to true in the given context; otherwise {@code false}
     */
    public boolean eval(Expression expr, DataContext ctx) {
        return switch (expr) {
            case Expression.StringCondition c -> evalStringCondition(c, ctx);
            case Expression.NumberCondition c -> evalNumberCondition(c, ctx);
            case Expression.BinaryExpression b -> {
                boolean left = eval(b.left(), ctx);
                boolean right = eval(b.right(), ctx);
                yield switch (b.operator()) {
                    case AND -> left && right;
                    case OR -> left || right;
                };
            }
        };
    }

    /**
     * Evaluates a single number condition expression against the data context.
     *
     * @param condition   the condition to evaluate
     * @param ctx the context containing data values
     * @return {@code true} if the condition is satisfied; otherwise {@code false}
     * @throws RuntimeException if the operator is unknown or parsing fails
     */
    private boolean evalNumberCondition(Expression.NumberCondition condition, DataContext ctx) {
        Optional<Object> actualValue = ctx.get(condition.field());

        if (actualValue.isPresent() && actualValue.get() instanceof Integer intValue) {
            return switch (condition.operator()) {
                case EQUAL -> intValue == condition.value();
                case GREATER_THAN_OR_EQUAL_TO -> intValue >= condition.value();
                case LESS_THAN_OR_EQUAL_TO -> intValue <= condition.value();
                case GREATER_THAN -> intValue > condition.value();
                case LESS_THAN -> intValue < condition.value();
            };
        }
        return false;
    }

    private boolean evalStringCondition(Expression.StringCondition c, DataContext ctx) {
        return ctx.get(c.field())
                .map(actualValue -> actualValue.equals(c.value())).orElse(false);
    }
}
