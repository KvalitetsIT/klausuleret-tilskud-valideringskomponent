package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class ExpressionModelEntityMapper implements Mapper<Expression, ExpressionEntity> {
    @Override
    public ExpressionEntity map(Expression expression) {
        return switch (expression) {
            case BinaryExpression b -> this.map(b);
            case StringConditionExpression s -> this.map(s);
            case NumberConditionExpression n -> this.map(n);
            case PreviousOrdinationConditionExpression p -> this.map(p);
        };
    }

    private ExpressionEntity.StringConditionEntity map(StringConditionExpression b) {
        return new ExpressionEntity.StringConditionEntity(null, b.field(), b.requiredValue());
    }

    private ExpressionEntity.NumberConditionEntity map(NumberConditionExpression b) {
        return new ExpressionEntity.NumberConditionEntity(null, b.field(), b.operator(), b.value());
    }

    private ExpressionEntity.PreviousOrdinationEntity map(PreviousOrdinationConditionExpression p) {
        return new ExpressionEntity.PreviousOrdinationEntity(null, p.atcCode(), p.formCode(), p.routeOfAdministrationCode());
    }

    private ExpressionEntity.BinaryExpressionEntity map(BinaryExpression b) {
        return new ExpressionEntity.BinaryExpressionEntity(null, this.map(b.left()), b.operator(), this.map((b.right())));
    }


}
