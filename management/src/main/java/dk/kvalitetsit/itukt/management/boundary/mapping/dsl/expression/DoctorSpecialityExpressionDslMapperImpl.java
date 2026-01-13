package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.openapitools.model.DoctorSpecialityCondition;

import java.util.List;

class DoctorSpecialityExpressionDslMapperImpl implements ExpressionDslMapper<DoctorSpecialityCondition> {

    @Override
    public String merge(List<DoctorSpecialityCondition> expressions) {
        return ExpressionDtoDslMapper.mergeConditions(Identifier.DOCTOR_SPECIALITY, expressions, DoctorSpecialityCondition::getValue);
    }

    @Override
    public Dsl map(DoctorSpecialityCondition entry) {
        return new Dsl(Identifier.DOCTOR_SPECIALITY + " = " + entry.getValue(), Dsl.Type.CONDITION);
    }
}