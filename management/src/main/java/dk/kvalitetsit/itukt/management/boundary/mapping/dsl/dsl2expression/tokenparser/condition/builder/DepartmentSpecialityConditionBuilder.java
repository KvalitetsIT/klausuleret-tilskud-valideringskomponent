package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.Condition;
import org.openapitools.model.DepartmentSpecialityCondition;
import org.openapitools.model.Operator;

public class DepartmentSpecialityConditionBuilder implements ConditionBuilder {
    @Override
    public Identifier identifier() {
        return Identifier.DEPARTMENT_SPECIALITY;
    }

    @Override
    public DepartmentSpecialityCondition build(Operator operator, Condition.Value value) {
        if (operator != Operator.EQUAL) {
            throw new DslParserException("Unsupported operator for department speciality condition: " + operator);
        }
        return new DepartmentSpecialityCondition(value.asSimple().value(), ExpressionType.DEPARTMENT_SPECIALITY);
    }
}
