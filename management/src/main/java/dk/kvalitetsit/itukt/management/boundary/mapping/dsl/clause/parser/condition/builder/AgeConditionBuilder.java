package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.Operator;

public class AgeConditionBuilder implements ConditionBuilder {
    @Override
    public Identifier identifier() {
        return Identifier.AGE;
    }

    @Override
    public AgeCondition build(Operator operator, String value) {
        return new AgeCondition(operator, Integer.parseInt(value), ExpressionType.AGE);
    }
}
