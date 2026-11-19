package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.openapitools.model.*;

public class MapperFactory {
    private final StringToDslValueMapper stringToDslValueMapper = new StringToDslValueMapper();

    public ExpressionDslMapper<IndicationCondition> getIndicationConditionExpressionDslMapper() {
        return new SimpleConditionExpressionDslMapper<>(Identifier.INDICATION, stringToDslValueMapper, IndicationCondition::getValue);
    }

    public ExpressionDslMapper<DoctorSpecialityCondition> getDoctorSpecialityConditionExpressionDslMapper() {
        return new SimpleConditionExpressionDslMapper<>(Identifier.DOCTOR_SPECIALITY, stringToDslValueMapper, DoctorSpecialityCondition::getValue);
    }

    public ExpressionDslMapper<AgeCondition> getAgeConditionExpressionDslMapper() {
        return new AgeConditionDslMapperImpl();
    }

    public Mapper<BinaryExpression, Dsl> getBinaryExpressionExpressionDslMapper(ExpressionDtoDslMapper expressionDtoDslMapper) {
        return new BinaryExpressionDslMapperImpl(expressionDtoDslMapper);
    }

    public ExpressionDslMapper<ExistingDrugMedicationCondition> getExistingDrugMedicationConditionExpressionDslMapper() {
        return new ExistingDrugMedicationExpressionDslMapperImpl(stringToDslValueMapper);
    }

    public ExpressionDslMapper<DepartmentSpecialityCondition> getDepartmentSpecialityExpressionDslMapper() {
        return new SimpleConditionExpressionDslMapper<>(Identifier.DEPARTMENT_SPECIALITY, stringToDslValueMapper, DepartmentSpecialityCondition::getSpeciality);
    }

}
