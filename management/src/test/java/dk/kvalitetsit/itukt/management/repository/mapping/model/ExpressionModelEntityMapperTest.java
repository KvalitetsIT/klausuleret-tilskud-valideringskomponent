package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.model.BinaryExpression;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedication;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.repository.entity.Field;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExpressionModelEntityMapperTest {

    @InjectMocks
    private ExpressionModelEntityMapper mapper;

    @BeforeEach
    void setup() {
        this.mapper = new ExpressionModelEntityMapper();
    }

    @Test
    public void testMapping() {
        Assertions.assertThat(mapper.map(MockFactory.EXPRESSION_1_MODEL))
                .usingRecursiveComparison()
                .ignoringFields("id", "left.id", "right.id", "right.left.id", "right.right.id", "right.left.left.id", "right.left.right.id")
                .isEqualTo(MockFactory.EXPRESSION_1_ENTITY);
    }

    @Test
    void testMappingWithExistingDrugMedicationConditions() {
        var expression = new BinaryExpression(
                new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("atc1", "form1", "adm1")),
                BinaryExpression.Operator.AND,
                new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("atc2", "form2", "adm2"))
        );

        var mappedExpression = mapper.map(expression);

        var expectedExpression = new ExpressionEntity.BinaryExpressionEntity(
                null,
                new ExpressionEntity.ExistingDrugMedicationConditionEntity(null, "atc1", "form1", "adm1"),
                BinaryExpression.Operator.AND,
                new ExpressionEntity.ExistingDrugMedicationConditionEntity(null, "atc2", "form2", "adm2")
        );
        Assertions.assertThat(mappedExpression)
                .usingRecursiveComparison()
                .ignoringFields("id", "left.id", "right.id")
                .isEqualTo(expectedExpression);
    }

    @Test
    public void testMappingWithSpecialityCondition(){
        var expression = new dk.kvalitetsit.itukt.common.model.DoctorSpecialityConditionExpression("læge");
        var mappedExpression = mapper.map(expression);
        var expectedExpression = new ExpressionEntity.StringConditionEntity(Field.DOCTOR_SPECIALITY, "læge");
        assertEquals(expectedExpression, mappedExpression);
    }
}