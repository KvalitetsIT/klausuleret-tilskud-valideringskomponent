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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

        when(parent.toDsl(left)).thenReturn(new Dsl("left", Dsl.Type.CONDITION));
        when(parent.toDsl(right)).thenReturn(new Dsl("right", Dsl.Type.CONDITION));


        Dsl expected = new Dsl("left og right", Dsl.Type.AND);
        Dsl actual = mapper.map(subject);
        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void map_givenAValidIBinaryExpressions_whenMap_thenReturnExpectedDsl() {

        var left = Mockito.mock(AgeCondition.class);
        var right = Mockito.mock(IndicationCondition.class);

        var subject = new BinaryExpression().left(left).operator(BinaryOperator.OR).right(right);
        String leftDsl = Identifier.AGE + " = 20";
        String rightDsl = Identifier.INDICATION + " = indication1";
        var expected = new Dsl(leftDsl + " eller " + rightDsl, Dsl.Type.OR);

        when(parent.mergeConditions(List.of(left))).thenReturn(leftDsl);
        when(parent.mergeConditions(List.of(right))).thenReturn(rightDsl);

        var actual = mapper.map(subject);

        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void merge_givenDslWithTwoIndicationConditionsWithOrOperator_whenMap_thenReturnExpectedDsl() {
        IndicationCondition left = mock(IndicationCondition.class);
        IndicationCondition right = mock(IndicationCondition.class);

        BinaryExpression subject = new BinaryExpression(
                left,
                BinaryOperator.OR,
                right,
                ExpressionType.BINARY
        );

        when(parent.mergeConditions(List.of(left, right))).thenReturn("left eller right");

        Dsl actual = mapper.map(subject);

        Dsl expected = new Dsl("left eller right", Dsl.Type.CONDITION);
        assertEquals(expected, actual, "Unexpected mapping of: " + subject);

        verify(parent).mergeConditions(List.of(left, right));
        verifyNoMoreInteractions(parent);
    }

    @Test
    void merge_givenOrChainOfMixedConditionTypes_whenMap_thenGroupsByType() {
        // given
        IndicationCondition indicationCondition1 = mock(IndicationCondition.class);
        IndicationCondition indicationCondition2 = mock(IndicationCondition.class);
        AgeCondition ageCondition = mock(AgeCondition.class);

        BinaryExpression inner = new BinaryExpression(
                indicationCondition1,
                BinaryOperator.OR,
                indicationCondition2,
                ExpressionType.BINARY
        );
        BinaryExpression subject = new BinaryExpression(
                inner,
                BinaryOperator.OR,
                ageCondition,
                ExpressionType.BINARY
        );

        when(parent.mergeConditions(List.of(indicationCondition1, indicationCondition2))).thenReturn("indicationConditions");
        when(parent.mergeConditions(List.of(ageCondition))).thenReturn("age");

        Dsl actual = mapper.map(subject);

        assertEquals("indicationConditions eller age", actual.dsl());
        assertEquals(Dsl.Type.OR, actual.type());

        verify(parent).mergeConditions(List.of(indicationCondition1, indicationCondition2));
        verify(parent).mergeConditions(List.of(ageCondition));
    }

    @Test
    void merge_givenSingleOrCondition_whenMap_thenReturnsConditionDsl() {
        IndicationCondition left = mock(IndicationCondition.class);
        IndicationCondition right = mock(IndicationCondition.class);

        BinaryExpression subject = new BinaryExpression(
                left,
                BinaryOperator.OR,
                right,
                ExpressionType.BINARY
        );

        when(parent.mergeConditions(List.of(left, right))).thenReturn("blaah");

        Dsl actual = mapper.map(subject);

        assertEquals("blaah", actual.dsl());
        assertEquals(Dsl.Type.CONDITION, actual.type());
    }

    @Test
    void map_givenAndBetweenTwoConditions_whenMap_thenNoParentheses() {
        Expression left = mock(IndicationCondition.class);
        Expression right = mock(AgeCondition.class);
        BinaryExpression expr = new BinaryExpression(left, BinaryOperator.AND, right, ExpressionType.BINARY);

        when(parent.toDsl(left)).thenReturn(new Dsl("A", Dsl.Type.CONDITION));
        when(parent.toDsl(right)).thenReturn(new Dsl("B", Dsl.Type.CONDITION));

        Dsl result = mapper.map(expr);

        assertEquals("A og B", result.dsl());
        assertEquals(Dsl.Type.AND, result.type());
    }

    @Test
    void map_givenAndWithRightSideOr_whenMap_thenParenthesizeRight() {
        Expression left = mock(IndicationCondition.class);
        Expression rightLeft = mock(AgeCondition.class);
        Expression rightRight = mock(ExistingDrugMedicationCondition.class);

        BinaryExpression orExpr = new BinaryExpression(rightLeft, BinaryOperator.OR, rightRight, ExpressionType.BINARY);
        BinaryExpression expr = new BinaryExpression(left, BinaryOperator.AND, orExpr, ExpressionType.BINARY);

        when(parent.toDsl(left)).thenReturn(new Dsl("A", Dsl.Type.CONDITION));
        when(parent.toDsl(orExpr)).thenReturn(new Dsl("B eller C", Dsl.Type.OR));

        Dsl result = mapper.map(expr);

        assertEquals("A og (B eller C)", result.dsl());
        assertEquals(Dsl.Type.AND, result.type());
    }

    @Test
    void map_givenAndWithLeftAndRightOr_whenMap_thenParenthesizeBothSides() {
        Expression leftLeft = mock(IndicationCondition.class);
        Expression leftRight = mock(AgeCondition.class);
        Expression rightLeft = mock(ExistingDrugMedicationCondition.class);
        Expression rightRight = mock(IndicationCondition.class);

        BinaryExpression leftOr = new BinaryExpression(leftLeft, BinaryOperator.OR, leftRight, ExpressionType.BINARY);
        BinaryExpression rightOr = new BinaryExpression(rightLeft, BinaryOperator.OR, rightRight, ExpressionType.BINARY);
        BinaryExpression expr = new BinaryExpression(leftOr, BinaryOperator.AND, rightOr, ExpressionType.BINARY);

        when(parent.toDsl(leftOr)).thenReturn(new Dsl("A eller B", Dsl.Type.OR));
        when(parent.toDsl(rightOr)).thenReturn(new Dsl("C eller D", Dsl.Type.OR));

        Dsl result = mapper.map(expr);

        assertEquals("(A eller B) og (C eller D)", result.dsl());
        assertEquals(Dsl.Type.AND, result.type());
    }

}