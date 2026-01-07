package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.*;

public class MapperFactory {

    public ExpressionDslMapper<IndicationCondition> getIndicationConditionExpressionDslMapper() {
        return new IndicationExpressionDslMapperImpl();
    }

    public ExpressionDslMapper<DoctorSpecialityCondition> getDoctorSpecialityConditionExpressionDslMapper() {
        return new SpecialityExpressionDslMapperImpl();
    }

    public ExpressionDslMapper<AgeCondition> getAgeConditionExpressionDslMapper() {
        return new AgeConditionDslMapperImpl();
    }

    public Mapper<BinaryExpression, Dsl> getBinaryExpressionExpressionDslMapper(ExpressionDtoDslMapper expressionDtoDslMapper) {
        return new BinaryExpressionDslMapperImpl(expressionDtoDslMapper);
    }

    public ExpressionDslMapper<ExistingDrugMedicationCondition> getExistingDrugMedicationConditionExpressionDslMapper() {
        return new ExistingDrugMedicationExpressionDslMapperImpl();
    }

    public ExpressionDslMapper<DepartmentCondition> getDepartmentConditionExpressionDslMapper() {
        return new DepartmentConditionExpressionDslMapperImpl();
    }

}
