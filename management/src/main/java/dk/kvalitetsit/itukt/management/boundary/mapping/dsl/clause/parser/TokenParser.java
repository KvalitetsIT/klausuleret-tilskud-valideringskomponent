package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser;


public interface TokenParser<T> {
    T parse(TokenIterator tokens);
    boolean canParse(TokenIterator tokens);
}
