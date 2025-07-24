package dk.kvalitetsit.klaus.boundary.mapping;



import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Expression;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.Condition;
import org.openapitools.model.ParenthesizedExpression;

public class ExpressionMapper implements Mapper<org.openapitools.model.Expression, Expression> {

    private final OperatorDtoModelMapper operatorDtoModelMapper = new OperatorDtoModelMapper();

    @Override
    public Expression map(org.openapitools.model.Expression expression) {
        return switch (expression) {
            case BinaryExpression b -> this.map(b);
            case Condition c -> this.map(c);
            case ParenthesizedExpression p -> this.map(p);
            default -> throw new IllegalStateException("Unexpected value: " + expression);
        };
    }

    private Expression.Condition map(Condition b) {
        return new Expression.Condition(b.getField(), operatorDtoModelMapper.map(b.getOperator()), b.getValues());
    }

    private Expression.BinaryExpression map(BinaryExpression b) {
        return new Expression.BinaryExpression(this.map(b.getLeft()), b.getOperator(), this.map((b.getRight())));
    }

    private Expression.ParenthesizedExpression map(ParenthesizedExpression b) {
        return new Expression.ParenthesizedExpression(this.map(b.getInner()));
    }
}
