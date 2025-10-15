package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.BinaryOperator;
import dk.kvalitetsit.itukt.common.model.Field;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.apache.commons.lang3.NotImplementedException;

public class ExpressionModelDslMapper implements Mapper<Expression.Persisted, String> {


    @Override
    public String map(Expression.Persisted entry) {
        return switch (entry) {
            case Expression.Persisted.Binary b -> map(b);
            case Expression.Persisted.Condition s -> switch (s.condition()) {
                case Condition.Age e -> this.map(e);
                case Condition.ExistingDrugMedication e -> this.map(e);
                case Condition.Indication e -> this.map(e);
            };
        };
    }

    private String map(Condition.Indication s) {
        return "(" + Field.INDICATION + " = " + s.requiredValue() + ")";
    }

    private String map(Condition.Age n) {
        return "(" + Field.AGE + " " + n.operator().getValue() + " " + n.value() + ")";
    }

    private String map(Expression.Persisted.Binary expression) {
        return map(expression.left()) + " " + map(expression.operator()) + " " + map(expression.right());
    }

    private String map(BinaryOperator operator) {
        return switch (operator) {
            case AND -> "og";
            case OR -> "eller";
        };
    }

    private String map(Condition.ExistingDrugMedication expression) {
        throw new NotImplementedException("IUAKT-106");
    }
}
