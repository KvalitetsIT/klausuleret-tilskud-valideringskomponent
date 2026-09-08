package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression;

import dk.kvalitetsit.itukt.management.exceptions.DslParserException;
import dk.kvalitetsit.itukt.management.exceptions.UnexpectedValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LexerTest {
    @InjectMocks
    private Lexer lexer;

    @Test
    void getTokens_WithUnknownToken_ThrowsException() {
        String input = "alder = @";

        var e = assertThrows(UnexpectedValueException.class, () -> lexer.getTokens(input));
        assertEquals("@", e.getValue());
    }

    @Test
    void getTokens_WithAllValidTokenTypes_ReturnsTokensWithUppercaseValues() throws DslParserException {
        String input = "alder \"æøÅ\" * og eller >= <= = > < i , ( ) [ ] { }";

        ArgumentCaptor<List> tokensCaptor = ArgumentCaptor.forClass(List.class);
        try (MockedStatic<TokenIterator> tokenIteratorMock = Mockito.mockStatic(TokenIterator.class)) {
            var expectedTokenIterator = Mockito.mock(TokenIterator.class);
            tokenIteratorMock.when(() -> TokenIterator.fromTokens(Mockito.anyList()))
                    .thenReturn(expectedTokenIterator);

            var tokens = lexer.getTokens(input);

            assertEquals(expectedTokenIterator, tokens);
            tokenIteratorMock.verify(() -> TokenIterator.fromTokens(tokensCaptor.capture()));
        }

        var expectedTokens = List.of(
                new Token(TokenType.IDENTIFIER, "ALDER"),
                new Token(TokenType.VALUE, "ÆØÅ"),
                new Token(TokenType.VALUE, "*"),
                new Token(TokenType.KEYWORD, "OG"),
                new Token(TokenType.KEYWORD, "ELLER"),
                new Token(TokenType.OPERATOR, ">="),
                new Token(TokenType.OPERATOR, "<="),
                new Token(TokenType.OPERATOR, "="),
                new Token(TokenType.OPERATOR, ">"),
                new Token(TokenType.OPERATOR, "<"),
                new Token(TokenType.OPERATOR, "I"),
                new Token(TokenType.SYMBOL, ","),
                new Token(TokenType.SYMBOL, "("),
                new Token(TokenType.SYMBOL, ")"),
                new Token(TokenType.SYMBOL, "["),
                new Token(TokenType.SYMBOL, "]"),
                new Token(TokenType.SYMBOL, "{"),
                new Token(TokenType.SYMBOL, "}")
        );
        assertEquals(expectedTokens.size(), tokensCaptor.getValue().size());
        expectedTokens.forEach(token -> assertTrue(tokensCaptor.getValue().contains(token), "Expected token: " + token + " not found in captured tokens: " + tokensCaptor.getValue()));
    }
}