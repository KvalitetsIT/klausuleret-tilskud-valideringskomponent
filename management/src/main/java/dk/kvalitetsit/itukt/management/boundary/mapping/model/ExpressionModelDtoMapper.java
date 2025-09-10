package dk.kvalitetsit.itukt.management.boundary.mapping.model;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.*;

public class ExpressionModelDtoMapper implements Mapper<Expression, org.openapitools.model.Expression> {

    private final Mapper<dk.kvalitetsit.itukt.common.model.Operator, Operator> mapper = new OperatorModelDtoMapper();

    @Override
    public org.openapitools.model.Expression map(Expression expression) {
        return switch (expression) {
            case Expression.BinaryExpression b -> this.map(b);
            case Expression.StringCondition s -> this.map(s);
            case Expression.NumberCondition n -> this.map(n);
       };
    }

    private StringCondition map(Expression.StringCondition s) {
        return new StringCondition(s.field(), s.value(), "StringCondition");
    }

    private NumberCondition map(Expression.NumberCondition n) {
        return new NumberCondition(n.field(), mapper.map(n.operator()), n.value(), "NumberCondition");
    }

    private BinaryExpression map(Expression.BinaryExpression b) {
        return new BinaryExpression(this.map(b.left()), BinaryOperator.fromValue(b.operator().toString()), this.map((b.right())), "BinaryExpression");
    }

}