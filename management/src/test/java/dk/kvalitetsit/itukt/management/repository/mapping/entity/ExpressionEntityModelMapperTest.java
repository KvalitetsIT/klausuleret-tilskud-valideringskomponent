package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.model.BinaryOperator;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExpressionEntityModelMapperTest {

    @InjectMocks
    private ExpressionEntityModelMapper mapper;


    @BeforeEach
    void setup() {
        this.mapper = new ExpressionEntityModelMapper();
    }

    @Test
    public void testMapping() {
        assertEquals(MockFactory.EXPRESSION_1_MODEL, mapper.map(MockFactory.EXPRESSION_1_ENTITY));
    }

    @Test
    public void testMappingWithExistingDrugMedicationConditions() {
        var expressionEntity = new ExpressionEntity.Persisted.BinaryExpression(
                1L,
                new ExpressionEntity.Persisted.ExistingDrugMedicationCondition(2L, "atc1", "form1", "adm1"),
                BinaryOperator.AND,
                new ExpressionEntity.Persisted.ExistingDrugMedicationCondition(3L, "atc2", "form2", "adm2")
        );

        var actual = mapper.map(expressionEntity);

        var expected = new Expression.Persisted.Binary(
                1L,
                new Expression.Persisted.Condition(2L, new Condition.ExistingDrugMedication("atc1", "form1", "adm1")),
                BinaryOperator.AND,
                new Expression.Persisted.Condition(3L, new Condition.ExistingDrugMedication("atc2", "form2", "adm2"))
        );

        assertEquals(expected, actual);
    }

}