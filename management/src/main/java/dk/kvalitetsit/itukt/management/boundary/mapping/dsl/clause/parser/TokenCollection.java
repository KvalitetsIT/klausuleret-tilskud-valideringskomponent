package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Token;

import java.util.List;

public class TokenCollection {
    private final List<Token> tokens;

    public TokenCollection(List<Token> tokens) {
        this.tokens = tokens;
    }

    public TokenIterator iterator() {
        return new TokenIterator(tokens);
    }

    public TokenCollection tokensBefore(Token token) {
        int index = tokens.indexOf(token);
        if (index == -1) {
            throw new DslParserException("Expected token '%s' not found".formatted(token.text()));
        }
        return new TokenCollection(tokens.subList(0, index));
    }

    public TokenCollection tokensAfter(Token token) {
        int index = tokens.indexOf(token);
        if (index == -1) {
            throw new DslParserException("Expected token '%s' not found".formatted(token.text()));
        }
        return new TokenCollection(tokens.subList(index + 1, tokens.size()));
    }
}
