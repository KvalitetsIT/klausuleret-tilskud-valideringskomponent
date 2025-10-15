package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Field;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class NotPersistedExpressionModelEntityMapper implements Mapper<Expression.NotPersisted, ExpressionEntity.NotPersisted> {

    @Override
    public ExpressionEntity.NotPersisted map(Expression.NotPersisted entry) {
        return switch (entry) {
            case Expression.NotPersisted.Binary e -> map(e);
            case Expression.NotPersisted.Condition condition -> switch (condition.condition()) {
                case Condition.Age e -> map(e);
                case Condition.ExistingDrugMedication e -> map(e);
                case Condition.Indication e -> map(e);
            };
        };
    }

    private ExpressionEntity.NotPersisted.StringConditionEntity map(Condition.Indication b) {
        return new ExpressionEntity.NotPersisted.StringConditionEntity(Field.INDICATION, b.requiredValue());
    }

    private ExpressionEntity.NotPersisted.NumberCondition map(Condition.Age b) {
        return new ExpressionEntity.NotPersisted.NumberCondition(Field.AGE, b.operator(), b.value());
    }

    private ExpressionEntity.NotPersisted.ExistingDrugMedicationCondition map(Condition.ExistingDrugMedication e) {
        return new ExpressionEntity.NotPersisted.ExistingDrugMedicationCondition(e.atcCode(), e.formCode(), e.routeOfAdministrationCode());
    }

    private ExpressionEntity.NotPersisted.BinaryExpression map(Expression.NotPersisted.Binary b) {
        return new ExpressionEntity.NotPersisted.BinaryExpression(this.map(b.left()), b.operator(), this.map((b.right())));
    }

}
