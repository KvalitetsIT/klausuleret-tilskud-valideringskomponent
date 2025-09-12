package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.NumberConditionExpression;
import dk.kvalitetsit.itukt.common.model.StringConditionExpression;
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

    private StringConditionExpression map(ExpressionEntity.StringConditionEntity b) {
        return new StringConditionExpression(b.field(), b.value());
    }

    private NumberConditionExpression map(ExpressionEntity.NumberConditionEntity n) {
        return new NumberConditionExpression(n.field(), n.operator(), n.value());
    }

    private BinaryExpression map(ExpressionEntity.BinaryExpressionEntity b) {
        return new BinaryExpression(this.map(b.left()), b.operator(), this.map((b.right())));
    }


}
