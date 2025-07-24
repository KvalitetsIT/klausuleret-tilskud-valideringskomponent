package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.service.model.DataContext;

import java.util.List;

/**
 * The {@code Evaluator} class is responsible for evaluating logical expressions
 * against a given {@link DataContext}. It supports various types of expressions
 * including conditions, binary expressions (like "og" / "eller"), and parenthesized expressions.
 * <p>
 * This class is typically used in a rule engine or query language parser to evaluate
 * whether a set of data satisfies a logical expression tree.
 */
class Evaluator {

    /**
     * Evaluates an expression against the provided data context.
     *
     * @param expr the expression to evaluate
     * @param ctx  the context containing data values for fields
     * @return {@code true} if the expression evaluates to true in the given context; otherwise {@code false}
     */
    public boolean eval(Expression expr, DataContext ctx) {
        return switch (expr) {
            case Expression.Condition c -> evalCondition(c, ctx);
            case Expression.BinaryExpression b -> {
                boolean left = eval(b.left(), ctx);
                boolean right = eval(b.right(), ctx);
                yield switch (b.operator()) {
                    case "og" -> left && right;
                    case "eller" -> left || right;
                    default -> throw new RuntimeException("Unknown logical operator: " + b.operator());
                };
            }
            case Expression.ParenthesizedExpression p -> eval(p.inner(), ctx);
        };
    }

    /**
     * Evaluates a single condition expression against the data context.
     *
     * Supported operators include:
     * <ul>
     *     <li>{@code =} - true if any actual value equals the expected value</li>
     *     <li>{@code i} - true if any actual value is contained in the expected list</li>
     *     <li>{@code >=}, {@code <=}, {@code >}, {@code <} - numeric comparisons</li>
     * </ul>
     *
     * @param c   the condition to evaluate
     * @param ctx the context containing data values
     * @return {@code true} if the condition is satisfied; otherwise {@code false}
     * @throws RuntimeException if the operator is unknown or parsing fails
     */
    private boolean evalCondition(Expression.Condition c, DataContext ctx) {
        List<String> actualValues = ctx.get(c.field());

        return switch (c.operator()) {
            case EQUAL -> actualValues.stream().anyMatch(v -> v.equals(c.values().getFirst()));
            case IN -> actualValues.stream().anyMatch(c.values()::contains);
            case GREATER_THAN_OR_EQUAL_TO, LESS_THAN_OR_EQUAL_TO, GREATER_THAN, LESS_THAN -> {
                int target = Integer.parseInt(c.values().getFirst());
                yield actualValues.stream()
                        .mapToInt(Integer::parseInt)
                        .anyMatch(actual -> switch (c.operator()) {
                            case GREATER_THAN_OR_EQUAL_TO -> actual >= target;
                            case LESS_THAN_OR_EQUAL_TO -> actual <= target;
                            case GREATER_THAN -> actual > target;
                            case LESS_THAN -> actual < target;
                            default -> false;
                        });
            }
        };
    }
}
