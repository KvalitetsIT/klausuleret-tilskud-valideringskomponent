package dk.kvalitetsit.klaus.boundary.mapping;


import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Expression;

public class ExpressionDslMapper implements Mapper<Expression, String> {
    @Override
    public String map(Expression entry) {
        String result;

        switch (entry) {
            case Expression.ParenthesizedExpression c -> result = "(" + map(c.inner()) + ")";
            case Expression.BinaryExpression b -> result = map(b.left()) + " " +  map(b.operator())+ " " + map(b.right());
            case Expression.Condition c -> result = "(" + c.field() + " " + c.operator().getValue() + " " + String.join(", ", c.values()) + ")";
        }

        return result;
    }

    private String map(Expression.BinaryExpression.BinaryOperator operator) {
        return switch (operator){
            case AND -> "og";
            case OR -> "eller";
        };
    }
}
