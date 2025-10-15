package dk.kvalitetsit.itukt.management.boundary.mapping.model;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import org.openapitools.model.*;

public class ExpressionModelDtoMapper implements Mapper<Expression.Persisted, org.openapitools.model.Expression> {

    private final Mapper<dk.kvalitetsit.itukt.common.model.Operator, Operator> mapper = new OperatorModelDtoMapper();

    @Override
    public org.openapitools.model.Expression map(Expression.Persisted expression) {
        return switch (expression) {
            case Expression.Persisted.Binary b -> this.map(b);
            case Expression.Persisted.Condition s -> switch (s.condition()) {
                case Condition.Age e -> this.map(e);
                case Condition.ExistingDrugMedication e -> this.map(e);
                case Condition.Indication e -> this.map(e);
            };
        };
    }

    private IndicationCondition map(Condition.Indication s) {
        return new IndicationCondition(s.requiredValue(), "StringCondition");
    }

    private AgeCondition map(Condition.Age n) {
        return new AgeCondition(mapper.map(n.operator()), n.value(), "NumberCondition");
    }

    private ExistingDrugMedicationCondition map(Condition.ExistingDrugMedication e) {
        return new ExistingDrugMedicationCondition(e.atcCode(), e.formCode(), e.routeOfAdministrationCode(), "ExistingDrugMedicationCondition");
    }

    private BinaryExpression map(Expression.Persisted.Binary b) {
        return new BinaryExpression(this.map(b.left()), BinaryOperator.fromValue(b.operator().toString()), this.map((b.right())), "BinaryExpression");
    }

}