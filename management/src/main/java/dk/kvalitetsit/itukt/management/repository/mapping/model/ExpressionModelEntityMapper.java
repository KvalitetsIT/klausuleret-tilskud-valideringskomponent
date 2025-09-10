package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class ExpressionModelEntityMapper implements Mapper<Expression, ExpressionEntity> {
    @Override
    public ExpressionEntity map(Expression expression) {
        return switch (expression) {
            case Expression.BinaryExpression b -> this.map(b);
            case Expression.StringCondition s -> this.map(s);
            case Expression.NumberCondition n -> this.map(n);
        };
    }

    private ExpressionEntity.StringConditionEntity map(Expression.StringCondition b) {
        return new ExpressionEntity.StringConditionEntity(null, b.field(), b.value());
    }

    private ExpressionEntity.NumberConditionEntity map(Expression.NumberCondition b) {
        return new ExpressionEntity.NumberConditionEntity(null, b.field(), b.operator(), b.value());
    }

    private ExpressionEntity.BinaryExpressionEntity map(Expression.BinaryExpression b) {
        return new ExpressionEntity.BinaryExpressionEntity(null, this.map(b.left()), b.operator(), this.map((b.right())));
    }


}
