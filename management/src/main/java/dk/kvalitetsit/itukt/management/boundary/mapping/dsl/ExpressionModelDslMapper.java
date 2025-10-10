package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.*;

public class ExpressionModelDslMapper implements Mapper<Expression, String> {

    @Override
    public String map(Expression entry) {
        return switch (entry) {
            case BinaryExpression b -> map(b);
            case IndicationConditionExpression s -> map(s);
            case AgeConditionExpression n -> map(n);
            case ExistingDrugMedicationConditionExpression e -> null; // TODO: IUAKT-106
        };
    }

    private String map(IndicationConditionExpression s) {
        return "(" + IndicationConditionExpression.field() + " = " + s.requiredValue() + ")";
    }

    private String map(AgeConditionExpression n) {
        return "(" + AgeConditionExpression.field() + " " + n.operator().getValue() + " " + n.value() + ")";
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
