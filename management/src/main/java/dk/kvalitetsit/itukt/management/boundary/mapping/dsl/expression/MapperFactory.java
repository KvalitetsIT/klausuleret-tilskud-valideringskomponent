package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.common.Mapper;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.ExistingDrugMedicationCondition;
import org.openapitools.model.IndicationCondition;

public class MapperFactory {

    public ExpressionDslMapper<IndicationCondition> getIndicationConditionExpressionDslMapper() {
        return new IndicationExpressionDslMapperImpl();
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
}
