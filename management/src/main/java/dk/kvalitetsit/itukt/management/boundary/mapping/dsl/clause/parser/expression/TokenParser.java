package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression;


import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenIterator;

public interface TokenParser<T> {
    boolean canParse(TokenIterator tokens);
    T parse(TokenIterator tokens);
}
