package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class NotPersistedExpressionEntityModelMapper implements Mapper<ExpressionEntity.NotPersisted, Expression.NotPersisted> {

    @Override
    public Expression.NotPersisted map(ExpressionEntity.NotPersisted entry) {
        return switch (entry) {
            case ExpressionEntity.NotPersisted.BinaryExpression e -> this.mapBinaryExpressionEntity(e);
            case ExpressionEntity.NotPersisted.ExistingDrugMedicationCondition e ->
                    this.mapExistingDrugMedicationConditionEntity(e);
            case ExpressionEntity.NotPersisted.NumberCondition e -> this.mapNumberConditionEntity(e);
            case ExpressionEntity.NotPersisted.StringConditionEntity e -> this.mapStringConditionEntity(e);
        };
    }


    private Expression.NotPersisted mapBinaryExpressionEntity(ExpressionEntity.NotPersisted.BinaryExpression e) {
        return new Expression.NotPersisted.Binary(this.map(e.left()), e.operator(), this.map((e.right())));
    }

    private Expression.NotPersisted mapExistingDrugMedicationConditionEntity(ExpressionEntity.NotPersisted.ExistingDrugMedicationCondition e) {
        return new Expression.NotPersisted.Condition(new Condition.ExistingDrugMedication(e.atcCode(), e.formCode(), e.routeOfAdministrationCode()));
    }

    private Expression.NotPersisted mapNumberConditionEntity(ExpressionEntity.NotPersisted.NumberCondition e) {
        return new Expression.NotPersisted.Condition(new Condition.Age(e.operator(), e.value()));
    }

    private Expression.NotPersisted mapStringConditionEntity(ExpressionEntity.NotPersisted.StringConditionEntity e) {
        return new Expression.NotPersisted.Condition(new Condition.Indication(e.value()));
    }

}
