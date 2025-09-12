package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.NumberConditionExpression;
import dk.kvalitetsit.itukt.common.model.StringConditionExpression;

public class ExpressionModelDslMapper implements Mapper<Expression, String> {

    @Override
    public String map(Expression entry) {
        return switch (entry) {
            case BinaryExpression b -> map(b);
            case StringConditionExpression s -> map(s);
            case NumberConditionExpression n -> map(n);
        };
    }

    private String map(StringConditionExpression s) {
        return "(" + s.field() + " = " + s.requiredValue() + ")";
    }

    private String map(NumberConditionExpression n) {
        return "(" + n.field() + " " + n.operator().getValue() + " " + n.value() + ")";
    }

    private String map(BinaryExpression expression) {
        return map(expression.left()) + " " + map(expression.operator()) + " " + map(expression.right());
    }

    private String map(BinaryExpression.Operator operator) {
        return switch (operator) {
            case AND -> "og";
            case OR -> "eller";
        };
    }
}
