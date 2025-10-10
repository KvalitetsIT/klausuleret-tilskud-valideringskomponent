package dk.kvalitetsit.itukt.management.boundary.mapping.model;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.AgeConditionExpression;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.IndicationConditionExpression;
import org.openapitools.model.*;

public class ExpressionModelDtoMapper implements Mapper<Expression, org.openapitools.model.Expression> {

    private final Mapper<dk.kvalitetsit.itukt.common.model.Operator, Operator> mapper = new OperatorModelDtoMapper();

    @Override
    public org.openapitools.model.Expression map(Expression expression) {
        return switch (expression) {
            case dk.kvalitetsit.itukt.common.model.BinaryExpression b -> this.map(b);
            case IndicationConditionExpression s -> this.map(s);
            case AgeConditionExpression n -> this.map(n);
            case ExistingDrugMedicationConditionExpression e -> this.map(e);
        };
    }

    private IndicationCondition map(IndicationConditionExpression s) {
        return new IndicationCondition(s.requiredValue(), "StringCondition");
    }

    private AgeCondition map(AgeConditionExpression n) {
        return new AgeCondition(mapper.map(n.operator()), n.value(), "NumberCondition");
    }

    private ExistingDrugMedicationCondition map(ExistingDrugMedicationConditionExpression e) {
        return new ExistingDrugMedicationCondition(e.atcCode(), e.formCode(), e.routeOfAdministrationCode(), "ExistingDrugMedicationCondition");
    }

    private BinaryExpression map(dk.kvalitetsit.itukt.common.model.BinaryExpression b) {
        return new BinaryExpression(this.map(b.left()), BinaryOperator.fromValue(b.operator().toString()), this.map((b.right())), "BinaryExpression");
    }

}