package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser;


public interface TokenParser<T> {
    boolean canParse(TokenIterator tokens);
    T parse(TokenIterator tokens);
}
