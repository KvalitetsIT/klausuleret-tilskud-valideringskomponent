package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser;


import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenIterator;
import dk.kvalitetsit.itukt.management.exceptions.DslParserException;

public interface TokenParser<T> {
    boolean canParse(TokenIterator tokens);
    T parse(TokenIterator tokens) throws DslParserException;
}
