package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression.condition.Condition;
import org.openapitools.model.DoctorSpecialityCondition;
import org.openapitools.model.Operator;

public class DoctorSpecialityConditionBuilder implements ConditionBuilder {
    @Override
    public Identifier identifier() {
        return Identifier.DOCTOR_SPECIALITY;
    }

    @Override
    public DoctorSpecialityCondition build(Operator operator, Condition.Value value) {
        if (operator != Operator.EQUAL) {
            throw new DslParserException("Unsupported operator for doctor speciality condition: " + operator);
        }
        return new DoctorSpecialityCondition(value.asSimple().value(), ExpressionType.DOCTOR_SPECIALITY);
    }
}
