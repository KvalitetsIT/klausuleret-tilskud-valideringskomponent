package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
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

    private Expression.StringCondition map(StringCondition b) {
        return new Expression.StringCondition(b.getField(), b.getValue());
    }

    private Expression.NumberCondition map(NumberCondition b) {
        return new Expression.NumberCondition(b.getField(), operatorDtoModelMapper.map(b.getOperator()), b.getValue());
    }

    private Expression.BinaryExpression map(BinaryExpression b) {
        return new Expression.BinaryExpression(this.map(b.getLeft()), Expression.BinaryExpression.BinaryOperator.valueOf(b.getOperator().getValue()), this.map((b.getRight())));
    }
}
