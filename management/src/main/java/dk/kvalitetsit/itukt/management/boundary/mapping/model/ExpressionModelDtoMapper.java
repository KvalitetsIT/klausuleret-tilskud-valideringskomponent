package dk.kvalitetsit.itukt.management.boundary.mapping.model;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.BinaryOperator;
import org.openapitools.model.Condition;
import org.openapitools.model.Operator;

public class ExpressionModelDtoMapper implements Mapper<Expression, org.openapitools.model.Expression> {

    private final Mapper<dk.kvalitetsit.itukt.common.model.Operator, Operator> mapper = new OperatorModelDtoMapper();

    @Override
    public org.openapitools.model.Expression map(Expression expression) {
        return switch (expression) {
            case Expression.BinaryExpression b -> this.map(b);
            case Expression.Condition c -> this.map(c);
       };
    }

    private Condition map(Expression.Condition b) {
        return new Condition(b.field(), mapper.map(b.operator()), b.value(), "Condition");
    }

    private BinaryExpression map(Expression.BinaryExpression b) {
        return new BinaryExpression(this.map(b.left()), BinaryOperator.fromValue(b.operator().toString()), this.map((b.right())), "BinaryExpression");
    }

}