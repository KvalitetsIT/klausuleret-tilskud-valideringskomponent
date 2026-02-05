package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Token;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.TokenType;

import java.util.Iterator;
import java.util.List;

public class TokenIterator {
    private final Iterator<Token> tokens;

    public TokenIterator(List<Token> tokens) {
        this.tokens = tokens.iterator();
    }

    /**
     * Returns the next token if it matches the expected type, otherwise throws an exception.
     */
    public Token nextWithType(TokenType expectedType) {
        validateHasNext();
        var token = tokens.next();
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
        var token = tokens.next();
        if (!List.of(expectedText).contains(token.text())) {
            throw new DslParserException("Unexpected value: " + token.text() + ", expected one of: " + expectedText);
        }
        return token;
    }

    /**
     * Expects that there are no more tokens, otherwise throws an exception.
     */
    public void expectNoMoreTokens() {
        if (tokens.hasNext()) {
            throw new DslParserException("Expected no more tokens, but found: " + tokens.next().text());
        }
    }

    private void validateHasNext() {
        if (!tokens.hasNext()) {
            throw new DslParserException("Unexpected end of tokens");
        }
    }
}
