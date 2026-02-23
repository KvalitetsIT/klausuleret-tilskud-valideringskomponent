package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenIteratorTest {

    @Test
    void nextWithType_WhenInitiatedWithoutTokens_ThrowsException() {
        var tokenIterator = TokenIterator.fromTokens(List.of());

        assertThrows(DslParserException.class, () -> tokenIterator.nextWithType(TokenType.VALUE));
    }

    @Test
    void nextWithType_WhenFirstTokenHasDifferentType_ThrowsException() {
        var tokenIterator = TokenIterator.fromTokens(List.of(
                new Token(TokenType.KEYWORD, "test"),
                new Token(TokenType.VALUE, "test")
        ));

        assertThrows(DslParserException.class, () -> tokenIterator.nextWithType(TokenType.VALUE));
    }

    @Test
    void nextWithType_ThriceWhenTokensHaveExpectedTypes_ReturnsTokens() {
        var token1 = new Token(TokenType.KEYWORD, "test1");
        var token2 = new Token(TokenType.OPERATOR, "test2");
        var token3 = new Token(TokenType.KEYWORD, "test3");
        var tokenIterator = TokenIterator.fromTokens(List.of(token1, token2, token3));

        var token1Result = tokenIterator.nextWithType(TokenType.KEYWORD);
        var token2Result = tokenIterator.nextWithType(TokenType.OPERATOR);
        var token3Result = tokenIterator.nextWithType(TokenType.KEYWORD);

        assertEquals(token1, token1Result);
        assertEquals(token2, token2Result);
        assertEquals(token3, token3Result);
    }

    @Test
    void nextWithText_WhenInitiatedWithoutTokens_ThrowsException() {
        var tokenIterator = TokenIterator.fromTokens(List.of());

        assertThrows(DslParserException.class, () -> tokenIterator.nextWithText("test"));
    }

    @Test
    void nextWithText_WhenFirstTokenHasDifferentText_ThrowsException() {
        var tokenIterator = TokenIterator.fromTokens(List.of(
                new Token(TokenType.KEYWORD, "jens"),
                new Token(TokenType.KEYWORD, "test")
        ));

        assertThrows(DslParserException.class, () -> tokenIterator.nextWithText("test"));
    }

    @Test
    void nextWithText_ThriceWhenTokensHaveExpectedTextButDifferentCasing_ReturnsTokens() {
        var token1 = new Token(TokenType.KEYWORD, "test1");
        var token2 = new Token(TokenType.OPERATOR, "TEST2");
        var token3 = new Token(TokenType.SYMBOL, "tEsT3!");
        var tokenIterator = TokenIterator.fromTokens(List.of(token1, token2, token3));

        var token1Result = tokenIterator.nextWithText("TEST1");
        var token2Result = tokenIterator.nextWithText("test2");
        var token3Result = tokenIterator.nextWithText("TEst3!");

        assertEquals(token1, token1Result);
        assertEquals(token2, token2Result);
        assertEquals(token3, token3Result);
    }

    @Test
    void nextHasText_WhenInitiatedWithoutTokens_ReturnsFalse() {
        var tokenIterator = TokenIterator.fromTokens(List.of());

        boolean hasText = tokenIterator.nextHasText("test");

        assertFalse(hasText);
    }

    @Test
    void nextHasText_WhenFirstTokenHasDifferentText_ReturnsFalse() {
        var tokenIterator = TokenIterator.fromTokens(List.of(
                new Token(TokenType.KEYWORD, "jens"),
                new Token(TokenType.KEYWORD, "test")
        ));

        boolean hasText = tokenIterator.nextHasText("test");

        assertFalse(hasText);
    }

    @Test
    void nextHasText_WhenFirstTokenHaveExpectedTextButDifferentCasing_ReturnsTrue() {
        var tokenIterator = TokenIterator.fromTokens(List.of(new Token(TokenType.KEYWORD, "test1")));

        var hasText = tokenIterator.nextHasText("TEST1");

        assertTrue(hasText);
    }

    @Test
    void nextHasType_WhenInitiatedWithoutTokens_ReturnsFalse() {
        var tokenIterator = TokenIterator.fromTokens(List.of());

        boolean hasType = tokenIterator.nextHasType(TokenType.VALUE);

        assertFalse(hasType);
    }

    @Test
    void nextHasType_WhenFirstTokenHasDifferentType_ReturnsFalse() {
        var tokenIterator = TokenIterator.fromTokens(List.of(
                new Token(TokenType.KEYWORD, "jens"),
                new Token(TokenType.VALUE, "test")
        ));

        boolean hasType = tokenIterator.nextHasType(TokenType.VALUE);

        assertFalse(hasType);
    }

    @Test
    void nextHasType_WhenFirstTokenHaveExpectedType_ReturnsTrue() {
        var tokenIterator = TokenIterator.fromTokens(List.of(new Token(TokenType.VALUE, "test1")));

        var hasType = tokenIterator.nextHasType(TokenType.VALUE);

        assertTrue(hasType);
    }

    @Test
    void expectNoMoreTokens_WhenInitiatedWithoutTokens_DoesNotThrow() {
        var tokenIterator = TokenIterator.fromTokens(List.of());

        assertDoesNotThrow(tokenIterator::expectNoMoreTokens);
    }

    @Test
    void expectNoMoreTokens_WithOneTokenAfterNextIsCalled_DoesNotThrow() {
        var tokenIterator = TokenIterator.fromTokens(List.of(new Token(TokenType.VALUE, "test")));
        tokenIterator.nextWithType(TokenType.VALUE);

        assertDoesNotThrow(tokenIterator::expectNoMoreTokens);
    }

    @Test
    void expectNoMoreTokens_WithTwoTokenAfterNextIsCalledOnce_ThrowsException() {
        var tokenIterator = TokenIterator.fromTokens(List.of(
                new Token(TokenType.VALUE, "test1"),
                new Token(TokenType.VALUE, "test2")
        ));
        tokenIterator.nextWithType(TokenType.VALUE);

        assertThrows(DslParserException.class, tokenIterator::expectNoMoreTokens);
    }
}