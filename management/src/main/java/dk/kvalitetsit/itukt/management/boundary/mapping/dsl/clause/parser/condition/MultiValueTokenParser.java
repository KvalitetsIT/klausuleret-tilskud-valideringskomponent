package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.TokenType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a multi-value token list. E.g. [value1, value2, value3]
 */
public class MultiValueTokenParser implements TokenParser<List<String>> {
    @Override
    public List<String> parse(TokenIterator tokens) {
        tokens.nextWithText("[");
        List<String> values = new ArrayList<>();

        do {
            String value = tokens.nextWithType(TokenType.VALUE).text();
            values.add(value);
        } while (tokens.nextWithText("]", ",").text().equals(","));
        return values;
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return tokens.nextHasText("[");
    }
}
