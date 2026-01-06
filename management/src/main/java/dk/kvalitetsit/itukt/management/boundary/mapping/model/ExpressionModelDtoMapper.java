package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import org.openapitools.model.*;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.Operator;

public class ExpressionModelDtoMapper implements Mapper<Expression, org.openapitools.model.Expression> {

    private final Mapper<dk.kvalitetsit.itukt.common.model.Operator, Operator> mapper = new OperatorModelDtoMapper();

    @Override
    public org.openapitools.model.Expression map(Expression expression) {
        return switch (expression) {
            case dk.kvalitetsit.itukt.common.model.BinaryExpression b -> this.map(b);
            case IndicationConditionExpression s -> this.map(s);
            case AgeConditionExpression n -> this.map(n);
            case ExistingDrugMedicationConditionExpression e -> this.map(e);
            case DoctorSpecialityConditionExpression a -> this.map(a);
            case DepartmentSpecialityConditionExpression departmentSpecialityConditionExpression -> this.map(departmentSpecialityConditionExpression);
        };
    }

    private DepartmentCondition map(DepartmentSpecialityConditionExpression s) {
        return new DepartmentCondition(s.requiredSpeciality(), ExpressionType.DEPARTMENT_SPECIALITY);
    }

    private IndicationCondition map(IndicationConditionExpression s) {
        return new IndicationCondition(s.requiredValue(), ExpressionType.INDICATION);
    }

    private DoctorSpecialityCondition map(DoctorSpecialityConditionExpression a) {
        return new DoctorSpecialityCondition(a.speciality(), ExpressionType.DOCTOR_SPECIALITY);
    }

    private AgeCondition map(AgeConditionExpression n) {
        return new AgeCondition(mapper.map(n.operator()), n.value(), ExpressionType.AGE);
    }

    private ExistingDrugMedicationCondition map(ExistingDrugMedicationConditionExpression e) {
        return new ExistingDrugMedicationCondition(
                e.existingDrugMedication().atcCode(),
                e.existingDrugMedication().formCode(),
                e.existingDrugMedication().routeOfAdministrationCode(),
                ExpressionType.EXISTING_DRUG_MEDICATION
        );
    }

    private BinaryExpression map(dk.kvalitetsit.itukt.common.model.BinaryExpression b) {
        return new BinaryExpression(this.map(b.left()), BinaryOperator.fromValue(b.operator().toString()), this.map((b.right())), ExpressionType.BINARY);
    }
}