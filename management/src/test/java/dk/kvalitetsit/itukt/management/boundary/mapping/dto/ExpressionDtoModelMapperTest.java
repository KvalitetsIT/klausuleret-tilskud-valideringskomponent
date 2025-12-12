package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedication;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.BinaryOperator;
import org.openapitools.model.ExistingDrugMedicationCondition;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExpressionDtoModelMapperTest {

    @InjectMocks
    private ExpressionDtoModelMapper mapper;

    @BeforeEach
    void setup() {
        this.mapper = new ExpressionDtoModelMapper();
    }
    @Test
    void map() {
        assertEquals(MockFactory.EXPRESSION_1_MODEL, this.mapper.map(MockFactory.EXPRESSION_1_DTO));
    }

    @Test
    void testMapWithExistingDrugMedicationConditions() {
        var expression = new BinaryExpression(
                new ExistingDrugMedicationCondition("atc1", "form1", "adm1", ExpressionType.EXISTING_DRUG_MEDICATION),
                BinaryOperator.AND,
                new ExistingDrugMedicationCondition("atc2", "form2", "adm2", ExpressionType.EXISTING_DRUG_MEDICATION),
                ExpressionType.BINARY
        );

        var mappedExpression = mapper.map(expression);

        var expectedExpression = new dk.kvalitetsit.itukt.common.model.BinaryExpression(
                new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("atc1", "form1", "adm1")),
                dk.kvalitetsit.itukt.common.model.BinaryExpression.Operator.AND,
                new ExistingDrugMedicationConditionExpression(new ExistingDrugMedication("atc2", "form2", "adm2"))
        );
        assertEquals(expectedExpression, mappedExpression);
    }
}