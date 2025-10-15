package dk.kvalitetsit.itukt.management.boundary.mapping.model;

import dk.kvalitetsit.itukt.common.model.BinaryOperator;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.ExistingDrugMedicationCondition;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExpressionModelDtoMapperTest {

    @InjectMocks
    private ExpressionModelDtoMapper mapper;

    @Test
    void map() {
        assertEquals(MockFactory.EXPRESSION_1_DTO, this.mapper.map(MockFactory.EXPRESSION_1_MODEL));
    }

    @Test
    void testMapWithExistingDrugMedicationConditions() {
        var expression = new Expression.Persisted.Binary(
                1L,
                new Expression.Persisted.Condition(2L, new Condition.ExistingDrugMedication("atc1", "form1", "adm1")),
                BinaryOperator.AND,
                new Expression.Persisted.Condition(3L, new Condition.ExistingDrugMedication("atc2", "form2", "adm2"))
        );

        var mappedExpression = mapper.map(expression);

        var expectedExpression = new BinaryExpression(
                new ExistingDrugMedicationCondition("atc1", "form1", "adm1", "ExistingDrugMedicationCondition"),
                org.openapitools.model.BinaryOperator.AND,
                new ExistingDrugMedicationCondition("atc2", "form2", "adm2", "ExistingDrugMedicationCondition"),
                "BinaryExpression"
        );
        assertEquals(expectedExpression, mappedExpression, "Values of mapped expression should match the input values");
    }
}