package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;

public class ExpressionModelDslMapper implements Mapper<Expression, String> {

    @Override
    public String map(Expression entry) {
        return switch (entry) {
            case Expression.BinaryExpression b -> map(b);
            case Expression.Condition c -> map(c);
        };
    }

    private String map(Expression.Condition c) {
        return "(" + c.field() + " " + c.operator().getValue() + " " + String.join(", ", c.value()) + ")";
    }

    private String map(Expression.BinaryExpression expression) {
        return map(expression.left()) + " " + map(expression.operator()) + " " + map(expression.right());
    }

    private String map(Expression.BinaryExpression.BinaryOperator operator) {
        return switch (operator) {
            case AND -> "og";
            case OR -> "eller";
        };
    }
}
