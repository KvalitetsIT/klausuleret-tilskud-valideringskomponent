package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

public class ExpressionModelEntityMapper implements Mapper<Expression, ExpressionEntity> {
    @Override
    public ExpressionEntity map(Expression expression) {
        return switch (expression) {
            case BinaryExpression b -> this.map(b);
            case IndicationConditionExpression s -> this.map(s);
            case AgeConditionExpression n -> this.map(n);
            case ExistingDrugMedicationConditionExpression p -> this.map(p);
            case DoctorSpecialityConditionExpression a -> this.map(a);
        };
    }

    private ExpressionEntity.StringConditionEntity map(IndicationConditionExpression b) {
        return new ExpressionEntity.StringConditionEntity(null, Field.INDICATION, b.requiredValue());
    }

    private ExpressionEntity.NumberConditionEntity map(AgeConditionExpression b) {
        return new ExpressionEntity.NumberConditionEntity(null, Field.AGE, b.operator(), b.value());
    }

    private ExpressionEntity.ExistingDrugMedicationConditionEntity map(ExistingDrugMedicationConditionExpression e) {
        return new ExpressionEntity.ExistingDrugMedicationConditionEntity(
                null,
                e.existingDrugMedication().atcCode(),
                e.existingDrugMedication().formCode(),
                e.existingDrugMedication().routeOfAdministrationCode()
        );
    }

    private ExpressionEntity.BinaryExpressionEntity map(BinaryExpression b) {
        return new ExpressionEntity.BinaryExpressionEntity(null, this.map(b.left()), b.operator(), this.map((b.right())));
    }

    private ExpressionEntity.StringConditionEntity map(DoctorSpecialityConditionExpression a) {
        return new ExpressionEntity.StringConditionEntity(Field.DOCTOR_SPECIALITY, a.speciality());
    }
}