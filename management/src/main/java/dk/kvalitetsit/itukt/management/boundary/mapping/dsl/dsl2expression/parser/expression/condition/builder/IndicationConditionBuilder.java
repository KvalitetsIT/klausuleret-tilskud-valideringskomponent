package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.expression.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.expression.condition.Condition;
import org.openapitools.model.IndicationCondition;
import org.openapitools.model.Operator;

public class IndicationConditionBuilder implements ConditionBuilder {
    @Override
    public Identifier identifier() {
        return Identifier.INDICATION;
    }

    @Override
    public IndicationCondition build(Operator operator, Condition.Value value) {
        if (operator != Operator.EQUAL) {
            throw new DslParserException("Unsupported operator for indication condition: " + operator);
        }
        return new IndicationCondition(value.asSimple().value(), ExpressionType.INDICATION);
    }
}
