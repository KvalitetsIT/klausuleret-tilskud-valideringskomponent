package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.BinaryOperator;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.ExistingDrugMedicationCondition;
import org.openapitools.model.IndicationCondition;

public class ExpressionDtoModelMapper implements Mapper<org.openapitools.model.Expression, Expression.NotPersisted> {

    private final OperatorDtoModelMapper operatorDtoModelMapper = new OperatorDtoModelMapper();

    @Override
    public Expression.NotPersisted map(org.openapitools.model.Expression expression) {
        return switch (expression) {
            case BinaryExpression b -> this.map(b);
            case IndicationCondition s -> this.map(s);
            case AgeCondition n -> this.map(n);
            case ExistingDrugMedicationCondition e -> this.map(e);
        };
    }

    private Expression.NotPersisted.Condition map(IndicationCondition b) {
        return new Expression.NotPersisted.Condition(new Condition.Indication(b.getValue()));
    }

    private Expression.NotPersisted.Condition map(AgeCondition b) {
        return new Expression.NotPersisted.Condition(new Condition.Age(operatorDtoModelMapper.map(b.getOperator()), b.getValue()));
    }

    private Expression.NotPersisted.Condition map(ExistingDrugMedicationCondition e) {
        return new Expression.NotPersisted.Condition(new Condition.ExistingDrugMedication(e.getAtcCode(), e.getFormCode(), e.getRouteOfAdministrationCode()));
    }

    private Expression.NotPersisted.Binary map(BinaryExpression b) {
        return new Expression.NotPersisted.Binary(
                this.map(b.getLeft()),
                BinaryOperator.valueOf(b.getOperator().getValue()),
                this.map((b.getRight())));
    }
}
