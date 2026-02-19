package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenIterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.AgeCondition;
import org.openapitools.model.BinaryExpression;
import org.openapitools.model.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BinaryExpressionTokenParserTest {
    @Mock
    private ParenthesizedExpressionTokenParser parenthesizedParser;

    @Mock
    private ConditionExpressionTokenParser conditionParser;

    @Mock
    private TokenIterator tokenIterator;

    @InjectMocks
    private BinaryExpressionTokenParser binaryParser;

    @Test
    void canParse_WhenNeitherParenthesizedOrConditionParserCanParse_ReturnsFalse() {
        Mockito.when(parenthesizedParser.canParse(tokenIterator)).thenReturn(false);
        Mockito.when(conditionParser.canParse(tokenIterator)).thenReturn(false);

        boolean canParse = binaryParser.canParse(tokenIterator);

        assertFalse(canParse);
    }

    @Test
    void canParse_WhenParenthesizedParserCanParse_ReturnsTrue() {
        Mockito.when(parenthesizedParser.canParse(tokenIterator)).thenReturn(true);

        boolean canParse = binaryParser.canParse(tokenIterator);

        assertTrue(canParse);
    }

    @Test
    void canParse_WhenConditionParserCanParse_ReturnsTrue() {
        Mockito.when(conditionParser.canParse(tokenIterator)).thenReturn(true);

        boolean canParse = binaryParser.canParse(tokenIterator);

        assertTrue(canParse);
    }

    @Test
    void parse_WithNeitherBinaryOperatorOrParenthesis_ParsesCondition() {
        Mockito.when(parenthesizedParser.canParse(tokenIterator)).thenReturn(false);
        Mockito.when(tokenIterator.nextHasText("eller")).thenReturn(false);
        Mockito.when(tokenIterator.nextHasText("og")).thenReturn(false);
        var parsedCondition = Mockito.mock(AgeCondition.class);
        Mockito.when(conditionParser.parse(tokenIterator)).thenReturn(parsedCondition);

        var result = binaryParser.parse(tokenIterator);

        assertEquals(parsedCondition, result);
        Mockito.verify(tokenIterator).nextHasText("eller");
        Mockito.verify(tokenIterator).nextHasText("og");
        Mockito.verifyNoMoreInteractions(tokenIterator);
        Mockito.verify(conditionParser, Mockito.times(1)).parse(tokenIterator);
        Mockito.verify(parenthesizedParser).canParse(tokenIterator);
        Mockito.verifyNoMoreInteractions(parenthesizedParser);
    }

    @Test
    void parse_WithNeitherBinaryOperatorButParenthesisCanParse_ParsesParenthesized() {
        Mockito.when(parenthesizedParser.canParse(tokenIterator)).thenReturn(true);
        Mockito.when(tokenIterator.nextHasText("eller")).thenReturn(false);
        Mockito.when(tokenIterator.nextHasText("og")).thenReturn(false);
        var parsedExpression = Mockito.mock(AgeCondition.class);
        Mockito.when(parenthesizedParser.parse(tokenIterator)).thenReturn(parsedExpression);

        var result = binaryParser.parse(tokenIterator);

        assertEquals(parsedExpression, result);
        Mockito.verify(tokenIterator).nextHasText("eller");
        Mockito.verify(tokenIterator).nextHasText("og");
        Mockito.verifyNoMoreInteractions(tokenIterator);
        Mockito.verifyNoInteractions(conditionParser);
        Mockito.verify(parenthesizedParser, Mockito.times(1)).parse(tokenIterator);
    }

    @Test
    void parse_WithAndOperatorAndConditions_ParsesConditionsAndReturnsAndExpression() {
        var condition1 = Mockito.mock(AgeCondition.class);
        var condition2 = Mockito.mock(AgeCondition.class);
        Mockito.when(parenthesizedParser.canParse(tokenIterator)).thenReturn(false);
        Mockito.when(tokenIterator.nextHasText("eller")).thenReturn(false);
        Mockito.when(tokenIterator.nextHasText("og")).thenReturn(true, false);
        Mockito.when(conditionParser.parse(tokenIterator)).thenReturn(condition1, condition2);

        var result = binaryParser.parse(tokenIterator);

        var binaryExpression = assertInstanceOf(BinaryExpression.class, result);
        assertEquals(condition1, binaryExpression.getLeft());
        assertEquals(condition2, binaryExpression.getRight());
        assertEquals(BinaryOperator.AND, binaryExpression.getOperator());
    }

    @Test
    void parse_WithAndOperatorAndParenthesizedExpressions_ParsesParenthesizedExpressionsAndReturnsAndExpression() {
        var parenthesizedExpression1 = Mockito.mock(AgeCondition.class);
        var parenthesizedExpression2 = Mockito.mock(AgeCondition.class);
        Mockito.when(parenthesizedParser.canParse(tokenIterator)).thenReturn(true);
        Mockito.when(tokenIterator.nextHasText("eller")).thenReturn(false);
        Mockito.when(tokenIterator.nextHasText("og")).thenReturn(true, false);
        Mockito.when(parenthesizedParser.parse(tokenIterator)).thenReturn(parenthesizedExpression1, parenthesizedExpression2);

        var result = binaryParser.parse(tokenIterator);

        var binaryExpression = assertInstanceOf(BinaryExpression.class, result);
        assertEquals(parenthesizedExpression1, binaryExpression.getLeft());
        assertEquals(parenthesizedExpression2, binaryExpression.getRight());
        assertEquals(BinaryOperator.AND, binaryExpression.getOperator());
    }

    @Test
    void parse_WithOrOperatorAndConditions_ParsesConditionsAndReturnsOrExpression() {
        var condition1 = Mockito.mock(AgeCondition.class);
        var condition2 = Mockito.mock(AgeCondition.class);
        Mockito.when(parenthesizedParser.canParse(tokenIterator)).thenReturn(false);
        Mockito.when(tokenIterator.nextHasText("eller")).thenReturn(true, false);
        Mockito.when(tokenIterator.nextHasText("og")).thenReturn(false);
        Mockito.when(conditionParser.parse(tokenIterator)).thenReturn(condition1, condition2);

        var result = binaryParser.parse(tokenIterator);

        var binaryExpression = assertInstanceOf(BinaryExpression.class, result);
        assertEquals(condition1, binaryExpression.getLeft());
        assertEquals(condition2, binaryExpression.getRight());
        assertEquals(BinaryOperator.OR, binaryExpression.getOperator());
    }

    @Test
    void parse_WithOrOperatorAndParenthesizedExpressions_ParsesParenthesizedExpressionsAndReturnsOrExpression() {
        var parenthesizedExpression1 = Mockito.mock(AgeCondition.class);
        var parenthesizedExpression2 = Mockito.mock(AgeCondition.class);
        Mockito.when(parenthesizedParser.canParse(tokenIterator)).thenReturn(true);
        Mockito.when(tokenIterator.nextHasText("eller")).thenReturn(true, false);
        Mockito.when(tokenIterator.nextHasText("og")).thenReturn(false);
        Mockito.when(parenthesizedParser.parse(tokenIterator)).thenReturn(parenthesizedExpression1, parenthesizedExpression2);

        var result = binaryParser.parse(tokenIterator);

        var binaryExpression = assertInstanceOf(BinaryExpression.class, result);
        assertEquals(parenthesizedExpression1, binaryExpression.getLeft());
        assertEquals(parenthesizedExpression2, binaryExpression.getRight());
        assertEquals(BinaryOperator.OR, binaryExpression.getOperator());
    }

    @Test
    void parse_WithOrAndAndOperatorsWithConditions_ParsesConditionsAndReturnsOrExpression() {
        var condition1 = Mockito.mock(AgeCondition.class);
        var condition2 = Mockito.mock(AgeCondition.class);
        var condition3 = Mockito.mock(AgeCondition.class);
        var condition4 = Mockito.mock(AgeCondition.class);
        Mockito.when(parenthesizedParser.canParse(tokenIterator)).thenReturn(false);
        Mockito.when(tokenIterator.nextHasText("eller")).thenReturn(true, false);
        Mockito.when(tokenIterator.nextHasText("og")).thenReturn(true, false, true, false);
        Mockito.when(conditionParser.parse(tokenIterator)).thenReturn(condition1, condition2, condition3, condition4);

        var result = binaryParser.parse(tokenIterator);

        var orExpression = assertInstanceOf(BinaryExpression.class, result);
        assertEquals(BinaryOperator.OR, orExpression.getOperator());
        var leftAndExpression = assertInstanceOf(BinaryExpression.class, orExpression.getLeft());
        assertEquals(condition1, leftAndExpression.getLeft());
        assertEquals(condition2, leftAndExpression.getRight());
        var rightAndExpression = assertInstanceOf(BinaryExpression.class, orExpression.getRight());
        assertEquals(condition3, rightAndExpression.getLeft());
        assertEquals(condition4, rightAndExpression.getRight());
    }
}