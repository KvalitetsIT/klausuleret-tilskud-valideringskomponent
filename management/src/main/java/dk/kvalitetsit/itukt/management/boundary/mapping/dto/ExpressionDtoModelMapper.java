package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.AgeConditionExpression;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.IndicationConditionExpression;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.ExistingDrugMedicationCondition;
import org.openapitools.model.IndicationCondition;

public class ExpressionDtoModelMapper implements Mapper<org.openapitools.model.Expression, Expression> {

    private final OperatorDtoModelMapper operatorDtoModelMapper = new OperatorDtoModelMapper();

    @Override
    public Expression map(org.openapitools.model.Expression expression) {
        return switch (expression) {
            case BinaryExpression b -> this.map(b);
            case IndicationCondition s -> this.map(s);
            case AgeCondition n -> this.map(n);
            case ExistingDrugMedicationCondition e -> this.map(e);
        };
    }

    private IndicationConditionExpression map(IndicationCondition b) {
        return new IndicationConditionExpression(b.getValue());
    }

    private AgeConditionExpression map(AgeCondition b) {
        return new AgeConditionExpression(operatorDtoModelMapper.map(b.getOperator()), b.getValue());
    }

    private ExistingDrugMedicationConditionExpression map(ExistingDrugMedicationCondition e) {
        return new ExistingDrugMedicationConditionExpression(e.getAtcCode(), e.getFormCode(), e.getRouteOfAdministrationCode());
    }

    private dk.kvalitetsit.itukt.common.model.BinaryExpression map(BinaryExpression b) {
        return new dk.kvalitetsit.itukt.common.model.BinaryExpression(
                this.map(b.getLeft()),
                dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.valueOf(b.getOperator().getValue()),
                this.map((b.getRight())));
    }
}
