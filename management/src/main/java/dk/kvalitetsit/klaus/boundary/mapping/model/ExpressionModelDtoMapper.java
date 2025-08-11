package dk.kvalitetsit.klaus.boundary.mapping.model;


import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Expression;
import org.openapitools.model.*;

public class ExpressionModelDtoMapper implements Mapper<Expression, org.openapitools.model.Expression> {

    private final Mapper<dk.kvalitetsit.klaus.model.Operator, Operator> mapper = new OperatorModelDtoMapper();

    @Override
    public org.openapitools.model.Expression map(Expression expression) {
        return switch (expression) {
            case Expression.BinaryExpression b -> this.map(b);
            case Expression.Condition c -> this.map(c);
            case Expression.ParenthesizedExpression p -> this.map(p);
        };
    }

    private Condition map(Expression.Condition b) {
        return new Condition(b.field(), mapper.map(b.operator()), b.values(), "Condition");
    }

    private BinaryExpression map(Expression.BinaryExpression b) {
        return new BinaryExpression(this.map(b.left()), BinaryOperator.fromValue(b.operator().getValue()), this.map((b.right())), "BinaryExpression");
    }

    private ParenthesizedExpression map(Expression.ParenthesizedExpression b) {
        return new ParenthesizedExpression(this.map(b.inner()), "ParenthesizedExpression");
    }
}