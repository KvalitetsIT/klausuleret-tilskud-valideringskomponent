package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import dk.kvalitetsit.itukt.management.exceptions.DslParserException;
import dk.kvalitetsit.itukt.management.exceptions.UnexpectedValueException;
import org.openapitools.model.DoctorSpecialityCondition;
import org.openapitools.model.Operator;

public class DoctorSpecialityConditionBuilder implements ConditionBuilder {
    @Override
    public Identifier identifier() {
        return Identifier.DOCTOR_SPECIALITY;
    }

    @Override
    public DoctorSpecialityCondition build(Operator operator, Condition.Value value) throws DslParserException {
        if (operator != Operator.EQUAL) {
            throw new UnexpectedValueException(operator.getValue());
        }
        return new DoctorSpecialityCondition(value.asSimple().value(), ExpressionType.DOCTOR_SPECIALITY);
    }
}
