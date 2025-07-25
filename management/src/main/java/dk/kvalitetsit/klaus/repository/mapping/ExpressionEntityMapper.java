package dk.kvalitetsit.klaus.repository.mapping;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.repository.model.ExpressionEntity;

public class ExpressionEntityMapper implements Mapper<Expression, ExpressionEntity> {
    @Override
    public ExpressionEntity map(Expression expression) {
        return switch (expression) {
            case Expression.BinaryExpression b -> this.map(b);
            case Expression.Condition c -> this.map(c);
            case Expression.ParenthesizedExpression p -> this.map(p);
        };
    }

    private ExpressionEntity.ConditionEntity map(Expression.Condition b) {
        return new ExpressionEntity.ConditionEntity(null, b.field(), b.operator(), b.values());
    }

    private ExpressionEntity.BinaryExpressionEntity map(Expression.BinaryExpression b) {
        return new ExpressionEntity.BinaryExpressionEntity(null, this.map(b.left()), b.operator().getValue(), this.map((b.right())));
    }

    private ExpressionEntity.ParenthesizedExpressionEntity map(Expression.ParenthesizedExpression b) {
        return new ExpressionEntity.ParenthesizedExpressionEntity(null, this.map(b.inner()));
    }

}
