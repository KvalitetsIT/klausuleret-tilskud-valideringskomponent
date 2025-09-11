package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void testMappingWithPreviousOrdinations() {
        var expression = new Expression.BinaryExpression(
                new Expression.PreviousOrdination("atc1", "form1", "adm1"),
                Expression.BinaryExpression.BinaryOperator.AND,
                new Expression.PreviousOrdination("atc2", "form2", "adm2")
        );

        var mappedExpression = mapper.map(expression);

        var expectedExpression = new ExpressionEntity.BinaryExpressionEntity(
                null,
                new ExpressionEntity.PreviousOrdinationEntity(null, "atc1", "form1", "adm1"),
                Expression.BinaryExpression.BinaryOperator.AND,
                new ExpressionEntity.PreviousOrdinationEntity(null, "atc2", "form2", "adm2")
        );
        Assertions.assertThat(mappedExpression)
                .usingRecursiveComparison()
                .ignoringFields("id", "left.id", "right.id")
                .isEqualTo(expectedExpression);
    }
}