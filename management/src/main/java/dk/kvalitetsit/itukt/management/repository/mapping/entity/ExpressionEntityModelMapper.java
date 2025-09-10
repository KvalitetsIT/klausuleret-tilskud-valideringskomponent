package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class ExpressionEntityModelMapper implements Mapper<ExpressionEntity, Expression> {

    @Override
    public Expression map(ExpressionEntity expression) {
        return switch (expression) {
            case ExpressionEntity.BinaryExpressionEntity b -> this.map(b);
            case ExpressionEntity.StringConditionEntity s -> this.map(s);
            case ExpressionEntity.NumberConditionEntity n -> this.map(n);
        };
    }

    private Expression.StringCondition map(ExpressionEntity.StringConditionEntity b) {
        return new Expression.StringCondition(b.field(), b.value());
    }

    private Expression.NumberCondition map(ExpressionEntity.NumberConditionEntity n) {
        return new Expression.NumberCondition(n.field(), n.operator(), n.value());
    }

    private Expression.BinaryExpression map(ExpressionEntity.BinaryExpressionEntity b) {
        return new Expression.BinaryExpression(this.map(b.left()), b.operator(), this.map((b.right())));
    }


}
