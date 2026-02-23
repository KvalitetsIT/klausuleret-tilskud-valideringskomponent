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
class StructuredValueTokenParserTest {
    @Mock
    private TokenIterator tokenIterator;

    @InjectMocks
    private StructuredValueTokenParser structuredValueTokenParser;

    @Test
    void canParse_WhenFirstTokenIsNotACurlyBracket_ReturnsFalse() {
        Mockito.when(tokenIterator.nextHasText("{")).thenReturn(false);

        boolean canParse = structuredValueTokenParser.canParse(tokenIterator);

        assertFalse(canParse);
    }

    @Test
    void canParse_WhenFirstTokenIsACurlyBracket_ReturnsTrue() {
        Mockito.when(tokenIterator.nextHasText("{")).thenReturn(true);

        boolean canParse = structuredValueTokenParser.canParse(tokenIterator);

        assertTrue(canParse);
    }

    @Test
    void parse_WithMultipleStructuredValues_ReturnsParsedValues() {
        Mockito.when(tokenIterator.nextWithText("{")).thenReturn(null);
        Mockito.when(tokenIterator.nextWithText("=")).thenReturn(null);
        Mockito.when(tokenIterator.nextWithText("}", ","))
                        .thenReturn(new Token(TokenType.SYMBOL, ","))
                        .thenReturn(new Token(TokenType.SYMBOL, "}"));
        Mockito.when(tokenIterator.nextWithType(TokenType.VALUE))
                .thenReturn(new Token(TokenType.VALUE, "a"))
                .thenReturn(new Token(TokenType.VALUE, "1"))
                .thenReturn(new Token(TokenType.VALUE, "b"))
                .thenReturn(new Token(TokenType.VALUE, "2"));

        var structuredValue = structuredValueTokenParser.parse(tokenIterator);

        assertEquals(2, structuredValue.values().size());
        assertEquals("1", structuredValue.values().get("a"));
        assertEquals("2", structuredValue.values().get("b"));
        var inOrder = Mockito.inOrder(tokenIterator);
        inOrder.verify(tokenIterator).nextWithText("{");
        inOrder.verify(tokenIterator).nextWithType(TokenType.VALUE);
        inOrder.verify(tokenIterator).nextWithText("=");
        inOrder.verify(tokenIterator).nextWithType(TokenType.VALUE);
        inOrder.verify(tokenIterator).nextWithText("}", ",");
        inOrder.verify(tokenIterator).nextWithType(TokenType.VALUE);
        inOrder.verify(tokenIterator).nextWithText("=");
        inOrder.verify(tokenIterator).nextWithType(TokenType.VALUE);
        inOrder.verify(tokenIterator).nextWithText("}", ",");
        Mockito.verifyNoMoreInteractions(tokenIterator);
    }
}