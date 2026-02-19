package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenIterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.AgeCondition;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParenthesizedExpressionTokenParserTest {
    @Mock
    private ExpressionTokenParser expressionTokenParser;

    @Mock
    private TokenIterator tokenIterator;

    @InjectMocks
    private ParenthesizedExpressionTokenParser parenthesizedExpressionTokenParser;

    @Test
    void canParse_WhenNextHasTextReturnsFalse_ReturnsFalse() {
        Mockito.when(tokenIterator.nextHasText("(")).thenReturn(false);

        boolean canParse = parenthesizedExpressionTokenParser.canParse(tokenIterator);

        assertFalse(canParse);
    }

    @Test
    void canParse_WhenNextHasTextReturnsTrue_ReturnsTrue() {
        Mockito.when(tokenIterator.nextHasText("(")).thenReturn(true);

        boolean canParse = parenthesizedExpressionTokenParser.canParse(tokenIterator);

        assertTrue(canParse);
    }

    @Test
    void parse_RemovesParenthesisTokensAndParsesExpression() {
        var parsedExpression = Mockito.mock(AgeCondition.class);
        Mockito.when(expressionTokenParser.parse(tokenIterator)).thenReturn(parsedExpression);

        var result = parenthesizedExpressionTokenParser.parse(tokenIterator);

        assertEquals(parsedExpression, result);
        var inOrder = Mockito.inOrder(tokenIterator, expressionTokenParser);
        inOrder.verify(tokenIterator).nextWithText("(");
        inOrder.verify(expressionTokenParser).parse(tokenIterator);
        inOrder.verify(tokenIterator).nextWithText(")");
    }
}