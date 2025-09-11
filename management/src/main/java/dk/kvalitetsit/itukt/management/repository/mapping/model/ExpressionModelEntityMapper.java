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
            case ExistingDrugMedicationConditionExpression p -> this.map(p);
        };
    }

    private ExpressionEntity.StringConditionEntity map(StringConditionExpression b) {
        return new ExpressionEntity.StringConditionEntity(null, b.field(), b.requiredValue());
    }

    private ExpressionEntity.NumberConditionEntity map(NumberConditionExpression b) {
        return new ExpressionEntity.NumberConditionEntity(null, b.field(), b.operator(), b.value());
    }

    private ExpressionEntity.ExistingDrugMedicationConditionEntity map(ExistingDrugMedicationConditionExpression e) {
        return new ExpressionEntity.ExistingDrugMedicationConditionEntity(null, e.atcCode(), e.formCode(), e.routeOfAdministrationCode());
    }

    private ExpressionEntity.BinaryExpressionEntity map(BinaryExpression b) {
        return new ExpressionEntity.BinaryExpressionEntity(null, this.map(b.left()), b.operator(), this.map((b.right())));
    }


}
