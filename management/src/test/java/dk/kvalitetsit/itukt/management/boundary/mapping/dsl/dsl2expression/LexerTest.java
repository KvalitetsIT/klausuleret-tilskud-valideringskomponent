package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class LexerTest {
    @InjectMocks
    private Lexer lexer;

    @Test
    void getTokens_WithUnknownToken_ThrowsException() {
        String input = "alder = @";

        var e = assertThrows(DslParserException.class, () -> lexer.getTokens(input));
        assertEquals("Unknown token: @", e.getMessage());
    }

    @Test
    void getTokens_WithAllValidTokenTypes_ReturnsTokensWithUppercaseValues() {
        String input = "a æøÅ * og eller >= <= = > < i , ( ) [ ] { }";

        try (MockedStatic<TokenIterator> tokenIteratorMock = Mockito.mockStatic(TokenIterator.class)) {
            var expectedTokenIterator = Mockito.mock(TokenIterator.class);
            tokenIteratorMock.when(() -> TokenIterator.fromTokens(Mockito.anyList()))
                    .thenReturn(expectedTokenIterator);

            var tokens = lexer.getTokens(input);

            assertEquals(expectedTokenIterator, tokens);
            var expectedTokens = List.of(
                    new Token(TokenType.VALUE, "A"),
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
            tokenIteratorMock.verify(() -> TokenIterator.fromTokens(expectedTokens));
        }
    }
}