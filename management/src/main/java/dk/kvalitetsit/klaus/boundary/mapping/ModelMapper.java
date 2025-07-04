package dk.kvalitetsit.klaus.boundary.mapping;


import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Expression;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.Condition;
import org.openapitools.model.ParenthesizedExpression;

public class ModelMapper implements Mapper<Expression, org.openapitools.model.Expression> {

    @Override
    public org.openapitools.model.Expression map(Expression expression) {
        return switch (expression) {
            case Expression.BinaryExpression b -> this.map(b);
            case Expression.Condition c -> this.map(c);
            case Expression.ParenthesizedExpression p -> this.map(p);
        };
    }

    private Condition map(Expression.Condition b) {
        return new Condition(b.field(), b.operator(), b.values(), "Condition");
    }

    private BinaryExpression map(Expression.BinaryExpression b) {
        return new BinaryExpression(this.map(b.left()), b.operator(), this.map((b.right())), "BinaryExpression");
    }

    private ParenthesizedExpression map(Expression.ParenthesizedExpression b) {
        return new ParenthesizedExpression(this.map(b.inner()), "ParenthesizedExpression");
    }
}