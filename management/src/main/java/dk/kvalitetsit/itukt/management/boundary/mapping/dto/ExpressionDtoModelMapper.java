package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.*;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedication;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.*;
import org.openapitools.model.BinaryExpression;

public class ExpressionDtoModelMapper implements Mapper<org.openapitools.model.Expression, Expression> {

    private final OperatorDtoModelMapper operatorDtoModelMapper = new OperatorDtoModelMapper();

    @Override
    public Expression map(org.openapitools.model.Expression expression) {
        return switch (expression) {
            case BinaryExpression b -> this.map(b);
            case IndicationCondition s -> this.map(s);
            case AgeCondition n -> this.map(n);
            case ExistingDrugMedicationCondition e -> this.map(e);
            case DoctorSpecialityCondition a -> this.map(a);
            case DepartmentCondition e -> this.map(e);
        };
    }

    private DepartmentConditionExpression map(DepartmentCondition b) {
        return new DepartmentConditionExpression(b.getSpeciality());
    }

    private IndicationConditionExpression map(IndicationCondition b) {
        return new IndicationConditionExpression(b.getValue());
    }

    private DoctorSpecialityConditionExpression map(DoctorSpecialityCondition a) {
        return new DoctorSpecialityConditionExpression(a.getValue());
    }

    private AgeConditionExpression map(AgeCondition b) {
        return new AgeConditionExpression(operatorDtoModelMapper.map(b.getOperator()), b.getValue());
    }

    private ExistingDrugMedicationConditionExpression map(ExistingDrugMedicationCondition e) {
        return new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication(e.getAtcCode(), e.getFormCode(), e.getRouteOfAdministrationCode()));
    }

    private dk.kvalitetsit.itukt.common.model.BinaryExpression map(BinaryExpression b) {
        return new dk.kvalitetsit.itukt.common.model.BinaryExpression(
                this.map(b.getLeft()),
                dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.valueOf(b.getOperator().getValue()),
                this.map((b.getRight())));
    }
}
