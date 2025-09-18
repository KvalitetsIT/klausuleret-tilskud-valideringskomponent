package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
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
    public void testMapping(){
        assertEquals(MockFactory.EXPRESSION_1_MODEL, mapper.map(MockFactory.EXPRESSION_1_ENTITY));
    }

    @Test
    public void testMappingWithExistingDrugMedicationConditions(){
        var expressionEntity = new ExpressionEntity.BinaryExpressionEntity(
                new ExpressionEntity.ExistingDrugMedicationConditionEntity(null, "atc1", "form1", "adm1"),
                BinaryExpression.Operator.AND,
                new ExpressionEntity.ExistingDrugMedicationConditionEntity(null, "atc2", "form2", "adm2"));

        var mappedExpression = mapper.map(expressionEntity);

        var expectedExpression = new BinaryExpression(
                new ExistingDrugMedicationConditionExpression("atc1", "form1", "adm1"),
                BinaryExpression.Operator.AND,
                new ExistingDrugMedicationConditionExpression("atc2", "form2", "adm2")
        );
        assertEquals(expectedExpression, mappedExpression);
    }

}