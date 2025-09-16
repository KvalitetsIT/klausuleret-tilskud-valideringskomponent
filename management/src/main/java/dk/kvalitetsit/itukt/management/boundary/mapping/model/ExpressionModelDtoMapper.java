package dk.kvalitetsit.itukt.management.boundary.mapping.model;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.NumberConditionExpression;
import dk.kvalitetsit.itukt.common.model.StringConditionExpression;
import org.openapitools.model.*;

public class ExpressionModelDtoMapper implements Mapper<Expression, org.openapitools.model.Expression> {

    private final Mapper<dk.kvalitetsit.itukt.common.model.Operator, Operator> mapper = new OperatorModelDtoMapper();

    @Override
    public org.openapitools.model.Expression map(Expression expression) {
        return switch (expression) {
            case dk.kvalitetsit.itukt.common.model.BinaryExpression b -> this.map(b);
            case StringConditionExpression s -> this.map(s);
            case NumberConditionExpression n -> this.map(n);
       };
    }

    private StringCondition map(StringConditionExpression s) {
        return new StringCondition(s.field().name(), s.requiredValue(), "StringCondition");
    }

    private NumberCondition map(NumberConditionExpression n) {
        return new NumberCondition(n.field().name(), mapper.map(n.operator()), n.value(), "NumberCondition");
    }

    private BinaryExpression map(dk.kvalitetsit.itukt.common.model.BinaryExpression b) {
        return new BinaryExpression(this.map(b.left()), BinaryOperator.fromValue(b.operator().toString()), this.map((b.right())), "BinaryExpression");
    }

}