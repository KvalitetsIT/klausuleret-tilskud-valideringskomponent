package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.model.BinaryOperator;
import dk.kvalitetsit.itukt.common.model.Condition;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static dk.kvalitetsit.itukt.common.model.Operator.GREATER_THAN_OR_EQUAL_TO;

@ExtendWith(MockitoExtension.class)
public class NotPersistedExpressionModelEntityMapperTest {

    @InjectMocks
    private NotPersistedExpressionModelEntityMapper mapper;

    @BeforeEach
    void setup() {
        this.mapper = new NotPersistedExpressionModelEntityMapper();
    }

    @Test
    public void testMapping() {

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


        Assertions.assertThat(mapper.map(expression))
                .usingRecursiveComparison()
                .ignoringFields("id", "left.id", "right.id", "right.left.id", "right.right.id", "right.left.left.id", "right.left.right.id")
                .isEqualTo(MockFactory.EXPRESSION_1_ENTITY);
    }

    @Test
    void testMappingWithExistingDrugMedicationConditions() {
        Expression.NotPersisted expression = new Expression.NotPersisted.Binary(
                new Expression.NotPersisted.Condition(new Condition.ExistingDrugMedication("atc1", "form1", "adm1")),
                BinaryOperator.AND,
                new Expression.NotPersisted.Condition(new Condition.ExistingDrugMedication("atc2", "form2", "adm2"))
        );

        var mappedExpression = mapper.map(expression);

        ExpressionEntity.NotPersisted expectedExpression = new ExpressionEntity.NotPersisted.BinaryExpression(
                new ExpressionEntity.NotPersisted.ExistingDrugMedicationCondition("atc1", "form1", "adm1"),
                BinaryOperator.AND,
                new ExpressionEntity.NotPersisted.ExistingDrugMedicationCondition("atc2", "form2", "adm2")
        );

        Assertions.assertThat(mappedExpression)
                .usingRecursiveComparison()
                .ignoringFields("id", "left.id", "right.id")
                .isEqualTo(expectedExpression);
    }
}