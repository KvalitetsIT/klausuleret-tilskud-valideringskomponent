package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TokenIterator {
    private final LinkedList<Token> tokens;

    public TokenIterator(List<Token> tokens) {
        this.tokens = new LinkedList<>(tokens);
    }

    /**
     * Returns the next token if it matches the expected type, otherwise throws an exception.
     */
    public Token nextWithType(TokenType expectedType) {
        validateHasNext();
        var token = tokens.pop();
        if (token.type() != expectedType) {
            throw new DslParserException("Unexpected token type: " + token.type() + ", expected: " + expectedType);
        }
        return token;
    }

    /**
     * Returns the next token if its text matches one of the expected values, otherwise throws an exception.
     */
    public Token nextWithText(String ... expectedText) {
        validateHasNext();
        var token = tokens.pop();
        if (Arrays.stream(expectedText).noneMatch(expected -> expected.equalsIgnoreCase(token.text()))) {
            throw new DslParserException("Unexpected value: " + token.text() + ", expected one of: " + String.join(", ", expectedText));
        }
        return token;
    }

    public boolean nextHasText(String expectedText) {
        return !tokens.isEmpty() && tokens.peek().text().equalsIgnoreCase(expectedText);
    }

    public boolean nextHasType(TokenType expectedType) {
        return !tokens.isEmpty() && tokens.peek().type() == expectedType;
    }

    /**
     * Expects that there are no more tokens, otherwise throws an exception.
     */
    public void expectNoMoreTokens() {
        if (!tokens.isEmpty()) {
            throw new DslParserException("Expected no more tokens, but found: " + tokens.peek().text());
        }
    }

    private void validateHasNext() {
        if (tokens.isEmpty()) {
            throw new DslParserException("Unexpected end of tokens");
        }
    }
}
