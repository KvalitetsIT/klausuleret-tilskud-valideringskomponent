package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.TokenType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenCollection;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a multi-value token list. E.g. [value1, value2, value3]
 */
public class MultiValueTokenParser implements TokenParser<List<String>> {
    @Override
    public List<String> parse(TokenCollection tokens) {
        TokenIterator iterator = tokens.iterator();
        iterator.nextWithText("[");
        List<String> values = new ArrayList<>();

        do {
            String value = iterator.nextWithType(TokenType.VALUE).text();
            values.add(value);
        } while (iterator.nextWithText("]", ",").text().equals(","));
        iterator.expectNoMoreTokens();
        return values;
    }
}
