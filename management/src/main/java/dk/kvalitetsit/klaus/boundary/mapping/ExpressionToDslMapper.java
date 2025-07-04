package dk.kvalitetsit.klaus.boundary.mapping;


import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Expression;

public class ExpressionToDslMapper implements Mapper<Expression, String> {
    @Override
    public String map(Expression entry) {
        String result = "";

        switch (entry) {
            case Expression.ParenthesizedExpression c -> {
                result = "(" + map(c.inner()) + ")";
            }
            case Expression.BinaryExpression b -> {
                String left = map(b.left());
                String right = map(b.right());
                result = left + " " + b.operator() + " " + right;
            }
            case Expression.Condition c -> {
                String values = String.join(", ", c.values());
                result = c.field() + " " + c.operator() + " (" + values + ")";
            }
            default -> throw new IllegalArgumentException("Unexpected expression: " + entry);
        }

        return result;
    }
}
