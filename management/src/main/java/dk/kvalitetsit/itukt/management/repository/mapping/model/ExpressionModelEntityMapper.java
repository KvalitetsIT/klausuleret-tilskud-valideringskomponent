package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class ExpressionModelEntityMapper implements Mapper<Expression, ExpressionEntity> {
    @Override
    public ExpressionEntity map(Expression expression) {
        return switch (expression) {
            case Expression.BinaryExpression b -> this.map(b);
            case Expression.Condition c -> this.map(c);
       };
    }

    private ExpressionEntity.ConditionEntity map(Expression.Condition b) {
        return new ExpressionEntity.ConditionEntity(null, b.field(), b.operator(), b.value());
    }

    private ExpressionEntity.BinaryExpressionEntity map(Expression.BinaryExpression b) {
        return new ExpressionEntity.BinaryExpressionEntity(null, this.map(b.left()), b.operator(), this.map((b.right())));
    }


}
