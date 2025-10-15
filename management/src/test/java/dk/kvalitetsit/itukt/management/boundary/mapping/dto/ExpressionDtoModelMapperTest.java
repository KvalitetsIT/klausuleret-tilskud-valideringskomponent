package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.model.BinaryOperator;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.ExistingDrugMedicationCondition;

import static dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO;
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

        Expression.NotPersisted.Binary expression = new Expression.NotPersisted.Binary(
                new Expression.NotPersisted.Condition(new Condition.Indication("C10BA03")),
                BinaryOperator.OR,
                new Expression.NotPersisted.Binary(
                        new Expression.NotPersisted.Binary(
                                new Expression.NotPersisted.Condition(new Condition.Indication("C10BA02")),
                                BinaryOperator.OR,
                                new Expression.NotPersisted.Condition(new Condition.Indication("C10BA05"))
                        ),
                        BinaryOperator.AND,
                        new Expression.NotPersisted.Condition(new Condition.Age(GREATER_THAN_OR_EQUAL_TO, 13))
                )
        );

        assertEquals(expression, this.mapper.map(MockFactory.EXPRESSION_1_DTO));
    }

    @Test
    void testMapWithExistingDrugMedicationConditions() {
        var expression = new BinaryExpression(
                new ExistingDrugMedicationCondition("atc1", "form1", "adm1", "ExistingDrugMedicationCondition"),
                org.openapitools.model.BinaryOperator.AND,
                new ExistingDrugMedicationCondition("atc2", "form2", "adm2", "ExistingDrugMedicationCondition"),
                "BinaryExpression"
        );

        var mappedExpression = mapper.map(expression);

        var expectedExpression = new Expression.NotPersisted.Binary(
                new Expression.NotPersisted.Condition(
                        new Condition.ExistingDrugMedication(
                                "atc1", "form1", "adm1"
                        )
                ),
                BinaryOperator.AND,

                new Expression.NotPersisted.Condition(
                        new Condition.ExistingDrugMedication(
                                "atc2", "form2", "adm2"
                        )
                )
        );
        assertEquals(expectedExpression, mappedExpression);
    }
}