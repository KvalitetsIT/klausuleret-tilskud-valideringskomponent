package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.openapitools.model.Expression;
import org.openapitools.model.Operator;

public interface ConditionBuilder {
    Identifier identifier();
    Expression build(Operator operator, Condition.Value value);
}
