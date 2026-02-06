package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.expression;


import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.TokenIterator;

public interface TokenParser<T> {
    boolean canParse(TokenIterator tokens);
    T parse(TokenIterator tokens);
}
