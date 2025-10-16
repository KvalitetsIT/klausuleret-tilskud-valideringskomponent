package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class PersistedExpressionEntityModelMapper implements Mapper<ExpressionEntity.Persisted, Expression.Persisted> {

    @Override
    public Expression.Persisted map(ExpressionEntity.Persisted entry) {
        return switch (entry) {
            case ExpressionEntity.Persisted.BinaryExpression e -> this.mapBinaryExpressionEntity(e);
            case ExpressionEntity.Persisted.ExistingDrugMedicationCondition e ->
                    this.mapExistingDrugMedicationConditionEntity(e);
            case ExpressionEntity.Persisted.NumberCondition e -> this.mapNumberConditionEntity(e);
            case ExpressionEntity.Persisted.StringCondition e -> this.mapStringConditionEntity(e);
        };
    }


    private Expression.Persisted mapBinaryExpressionEntity(ExpressionEntity.Persisted.BinaryExpression e) {
        return new Expression.Persisted.Binary(e.id(), this.map(e.left()), e.operator(), this.map((e.right())));
    }

    private Expression.Persisted mapExistingDrugMedicationConditionEntity(ExpressionEntity.Persisted.ExistingDrugMedicationCondition e) {
        return new Expression.Persisted.Condition(e.id(), new Condition.ExistingDrugMedication(e.atcCode(), e.formCode(), e.routeOfAdministrationCode()));
    }

    private Expression.Persisted mapNumberConditionEntity(ExpressionEntity.Persisted.NumberCondition e) {
        return new Expression.Persisted.Condition(e.id(), new Condition.Age(e.operator(), e.value()));
    }

    private Expression.Persisted mapStringConditionEntity(ExpressionEntity.Persisted.StringCondition e) {
        return new Expression.Persisted.Condition(e.id(), new Condition.Indication(e.value()));
    }
}