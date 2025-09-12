package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.NumberConditionExpression;
import dk.kvalitetsit.itukt.common.model.StringConditionExpression;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.NumberCondition;
import org.openapitools.model.StringCondition;

public class ExpressionDtoModelMapper implements Mapper<org.openapitools.model.Expression, Expression> {

    private final OperatorDtoModelMapper operatorDtoModelMapper = new OperatorDtoModelMapper();

    @Override
    public Expression map(org.openapitools.model.Expression expression) {
        return switch (expression) {
            case BinaryExpression b -> this.map(b);
            case StringCondition s -> this.map(s);
            case NumberCondition n -> this.map(n);
            default -> throw new IllegalStateException("Unexpected value: " + expression);
        };
    }

    private StringConditionExpression map(StringCondition b) {
        return new StringConditionExpression(Expression.Condition.Field.valueOf(b.getField()), b.getValue());
    }

    private NumberConditionExpression map(NumberCondition b) {
        return new NumberConditionExpression(Expression.Condition.Field.valueOf(b.getField()), operatorDtoModelMapper.map(b.getOperator()), b.getValue());
    }

    private dk.kvalitetsit.itukt.common.model.BinaryExpression map(BinaryExpression b) {
        return new dk.kvalitetsit.itukt.common.model.BinaryExpression(
                this.map(b.getLeft()),
                dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.valueOf(b.getOperator().getValue()),
                this.map((b.getRight())));
    }
}
