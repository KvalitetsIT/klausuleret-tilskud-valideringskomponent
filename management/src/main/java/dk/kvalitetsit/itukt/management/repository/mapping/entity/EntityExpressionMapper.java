package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

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
        return new Expression.BinaryExpression(this.map(b.left()), b.operator(), this.map((b.right())));
    }

    private Expression.ParenthesizedExpression map(ExpressionEntity.ParenthesizedExpressionEntity b) {
        return new Expression.ParenthesizedExpression(this.map(b.inner()));
    }

}
