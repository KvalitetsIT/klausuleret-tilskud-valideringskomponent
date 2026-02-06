package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.expression.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.TokenType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.parser.expression.TokenParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a list of values from tokens. E.g. [value1, value2, value3]
 */
public class MultiValueTokenParser implements TokenParser<List<Condition.Value>> {
    private final StructuredValueTokenParser structuredValueTokenParser;

    public MultiValueTokenParser(StructuredValueTokenParser structuredValueTokenParser) {
        this.structuredValueTokenParser = structuredValueTokenParser;
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return tokens.nextHasText("[");
    }

    @Override
    public List<Condition.Value> parse(TokenIterator tokens) {
        tokens.nextWithText("[");
        var values = new ArrayList<Condition.Value>();
        do {
            var value = structuredValueTokenParser.canParse(tokens) ?
                    structuredValueTokenParser.parse(tokens) :
                    new Condition.Value.Simple(tokens.nextWithType(TokenType.VALUE).text());
            values.add(value);
        } while (tokens.nextWithText("]", ",").text().equals(","));
        return values;
    }
}
