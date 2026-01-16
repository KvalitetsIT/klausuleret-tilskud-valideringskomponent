package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.openapitools.model.DepartmentSpecialityCondition;

import java.util.List;

class DepartmentSpecialityExpressionDslMapperImpl implements ExpressionDslMapper<DepartmentSpecialityCondition> {
    @Override
    public String merge(List<DepartmentSpecialityCondition> expressions) {
        return ExpressionDtoDslMapper.mergeConditions(Identifier.DEPARTMENT_SPECIALITY, expressions, DepartmentSpecialityCondition::getSpeciality);
    }

    @Override
    public Dsl map(DepartmentSpecialityCondition entry) {
        return new Dsl(Identifier.DEPARTMENT_SPECIALITY + " = " + entry.getSpeciality(), Dsl.Type.CONDITION);
    }
}
