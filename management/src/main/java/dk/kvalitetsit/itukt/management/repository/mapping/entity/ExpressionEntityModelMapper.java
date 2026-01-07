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
            case ExpressionEntity.ExistingDrugMedicationConditionEntity e -> this.map(e);
        };
    }

    private Expression.Condition map(ExpressionEntity.StringConditionEntity b) {
        return switch (b.field()) {
            case AGE -> throw new RuntimeException("Error mapping age entity");
            case INDICATION -> new IndicationConditionExpression(b.value());
            case DOCTOR_SPECIALITY -> new DoctorSpecialityConditionExpression(b.value());
            case EXISTING_DRUG_MEDICATION -> throw new RuntimeException("Error mapping existing medication entity");
            case DEPARTMENT_SPECIALITY -> new DepartmentSpecialityConditionExpression(b.value());
        };
    }

    private AgeConditionExpression map(ExpressionEntity.NumberConditionEntity n) {
        return new AgeConditionExpression(n.operator(), n.value());
    }

    private ExistingDrugMedicationConditionExpression map(ExpressionEntity.ExistingDrugMedicationConditionEntity e) {
        return new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication(e.atcCode(), e.formCode(), e.routeOfAdministrationCode()));
    }

    private BinaryExpression map(ExpressionEntity.BinaryExpressionEntity b) {
        return new BinaryExpression(this.map(b.left()), b.operator(), this.map((b.right())));
    }
}
