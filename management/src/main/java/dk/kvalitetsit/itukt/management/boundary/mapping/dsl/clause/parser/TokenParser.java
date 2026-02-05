package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser;


import java.util.Optional;

public interface TokenParser<T> {
    T parse(TokenCollection tokens);

    default Optional<T> tryParse(TokenCollection tokens) {
        try {
            return Optional.of(parse(tokens));
        } catch (DslParserException e) {
            return Optional.empty();
        }
    }
}
