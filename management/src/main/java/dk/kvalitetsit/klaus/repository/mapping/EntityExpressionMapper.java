package dk.kvalitetsit.klaus.repository.mapping;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.repository.model.ExpressionEntity;

public class EntityExpressionMapper implements Mapper<ExpressionEntity, Expression> {

    @Override
    public Expression map(ExpressionEntity expression) {
        return switch (expression) {
            case ExpressionEntity.BinaryExpressionEntity b -> this.map(b);
            case ExpressionEntity.ConditionEntity c -> this.map(c);
            case ExpressionEntity.ParenthesizedExpressionEntity p -> this.map(p);
        };
    }

    private Expression.Condition map(ExpressionEntity.ConditionEntity b) {
        return new Expression.Condition(b.field(), b.operator(), b.values());
    }

    private Expression.BinaryExpression map(ExpressionEntity.BinaryExpressionEntity b) {
        return new Expression.BinaryExpression(this.map(b.left()), Expression.BinaryExpression.BinaryOperator.fromValue(b.operator()), this.map((b.right())));
    }

    private Expression.ParenthesizedExpression map(ExpressionEntity.ParenthesizedExpressionEntity b) {
        return new Expression.ParenthesizedExpression(this.map(b.inner()));
    }

}
