package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.Operator;

public class AgeConditionBuilder implements ConditionBuilder {
    @Override
    public Identifier identifier() {
        return Identifier.AGE;
    }

    @Override
    public AgeCondition build(Operator operator, Condition.Value value) {
        return new AgeCondition(operator, Integer.parseInt(value.asSimple().value()), ExpressionType.AGE);
    }
}
