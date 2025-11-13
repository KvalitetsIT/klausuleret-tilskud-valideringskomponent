package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class BinaryExpressionDslMapperImplTest {

    @InjectMocks
    private BinaryExpressionDslMapperImpl mapper;

    @Mock
    private ExpressionDtoDslMapper parent;

    @Test
    void merge_givenDslWithTwoIndicationConditions_whenMap_thenReturnExpectedDsl() {
        IndicationCondition left = Mockito.mock(IndicationCondition.class);
        IndicationCondition right = Mockito.mock(IndicationCondition.class);

        var subject = new BinaryExpression(
                left,
                BinaryOperator.AND,
                right,
                ExpressionType.BINARY
        );

        Mockito.when(parent.toDsl(left)).thenReturn(new Dsl("left", Dsl.Type.CONDITION));
        Mockito.when(parent.toDsl(right)).thenReturn(new Dsl("right", Dsl.Type.CONDITION));


        Dsl expected = new Dsl("left og right", Dsl.Type.AND);
        Dsl actual = mapper.map(subject);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void map_givenAValidIBinaryExpressions_whenMap_thenReturnExpectedDsl() {

        var left = new AgeCondition(Operator.EQUAL, 20, ExpressionType.BINARY);
        var right = new IndicationCondition().type(ExpressionType.INDICATION).value("indication1");

        var subject = new BinaryExpression().left(left).operator(BinaryOperator.OR).right(right);
        var expected = new Dsl(Identifier.AGE + " = 20 eller " + Identifier.INDICATION + " = indication1", Dsl.Type.OR);


        Mockito.when(parent.mergeConditions(List.of(left))).thenReturn(Identifier.AGE + " = 20");
        Mockito.when(parent.mergeConditions(List.of(right))).thenReturn(Identifier.INDICATION + " = indication1");

        var actual = mapper.map(subject);

        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }
}