package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.IncompleteDslException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.UnexpectedValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TokenIterator {
    private final Logger logger = LoggerFactory.getLogger(TokenIterator.class);
    private final LinkedList<Token> tokens;

    public static TokenIterator fromTokens(List<Token> tokens) {
        return new TokenIterator(tokens);
    }

    private TokenIterator(List<Token> tokens) {
        this.tokens = new LinkedList<>(tokens);
    }

    /**
     * Returns the next token if it matches the expected type, otherwise throws an exception.
     */
    public Token nextWithType(TokenType expectedType) {
        validateHasNext();
        var token = tokens.pop();
        if (token.type() != expectedType) {
            logger.debug("Unexpected token type: {}, expected: {}", token.type(), expectedType);
            throw new UnexpectedValueException(token.text());
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
            logger.debug("Unexpected value: {}, expected one of: {}", token.text(), String.join(", ", expectedText));
            throw new UnexpectedValueException(token.text());
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
            throw new UnexpectedValueException(tokens.peek().text());
        }
    }

    private void validateHasNext() {
        if (tokens.isEmpty()) {
            throw new IncompleteDslException();
        }
    }
}
