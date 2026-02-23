package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.Token;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MultiValueTokenParserTest {
    @Mock
    private TokenIterator tokenIterator;

    @Mock
    private StructuredValueTokenParser structuredValueTokenParser;

    @InjectMocks
    private MultiValueTokenParser multiValueTokenParser;

    @Test
    void canParse_WhenFirstTokenIsNotAnOpenBracket_ReturnsFalse() {
        Mockito.when(tokenIterator.nextHasText("[")).thenReturn(false);

        boolean canParse = multiValueTokenParser.canParse(tokenIterator);

        assertFalse(canParse);
    }

    @Test
    void canParse_WhenFirstTokenIsAnOpenBracket_ReturnsTrue() {
        Mockito.when(tokenIterator.nextHasText("[")).thenReturn(true);

        boolean canParse = multiValueTokenParser.canParse(tokenIterator);

        assertTrue(canParse);
    }

    @Test
    void parse_WithStructuredAndSimpleValues_ReturnsParsedValues() {
        Mockito.when(tokenIterator.nextWithText("[")).thenReturn(null);
        Mockito.when(structuredValueTokenParser.canParse(tokenIterator))
                .thenReturn(true, false);
        var structuredValue = Mockito.mock(Condition.Value.Structured.class);
        Mockito.when(structuredValueTokenParser.parse(tokenIterator))
                .thenReturn(structuredValue);
        String simpleValue = "simpleValue";
        Mockito.when(tokenIterator.nextWithType(TokenType.VALUE))
                .thenReturn(new Token(TokenType.VALUE, simpleValue));
        Mockito.when(tokenIterator.nextWithText("]", ","))
                .thenReturn(new Token(TokenType.SYMBOL, ","))
                .thenReturn(new Token(TokenType.SYMBOL, "]"));

        var values = multiValueTokenParser.parse(tokenIterator);

        assertEquals(2, values.size());
        assertTrue(values.contains(structuredValue));
        assertTrue(values.contains(new Condition.Value.Simple(simpleValue)));
        var inOrder = Mockito.inOrder(tokenIterator, structuredValueTokenParser);
        inOrder.verify(tokenIterator).nextWithText("[");
        inOrder.verify(structuredValueTokenParser).parse(tokenIterator);
        inOrder.verify(tokenIterator).nextWithText("]", ",");
        inOrder.verify(tokenIterator).nextWithType(TokenType.VALUE);
        inOrder.verify(tokenIterator).nextWithText("]", ",");
    }
}