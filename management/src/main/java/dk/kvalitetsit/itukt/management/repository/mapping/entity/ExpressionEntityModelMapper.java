package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class ExpressionEntityModelMapper implements Mapper<ExpressionEntity, Expression> {

    @Override
    public Expression map(ExpressionEntity expression) {
        return switch (expression) {
            case ExpressionEntity.BinaryExpressionEntity b -> this.map(b);
            case ExpressionEntity.StringConditionEntity s -> this.map(s);
            case ExpressionEntity.NumberConditionEntity n -> this.map(n);
            case ExpressionEntity.PreviousOrdinationEntity p -> this.map(p);
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

    private PreviousOrdinationConditionExpression map(ExpressionEntity.PreviousOrdinationEntity p) {
        return new PreviousOrdinationConditionExpression(p.atcCode(), p.formCode(), p.routeOfAdministrationCode());
    }


}
