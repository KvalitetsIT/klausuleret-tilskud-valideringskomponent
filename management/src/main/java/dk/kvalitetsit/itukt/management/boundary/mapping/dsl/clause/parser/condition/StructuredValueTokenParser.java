package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.TokenType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenParser;

import java.util.HashMap;

public class StructuredValueTokenParser implements TokenParser<Condition.Value.Structured> {
    @Override
    public boolean canParse(TokenIterator tokens) {
        return tokens.nextHasText("{");
    }

    @Override
    public Condition.Value.Structured parse(TokenIterator tokens) {
        tokens.nextWithText("{");
        var structuredValue = new HashMap<String, String>();

        do {
            String key = tokens.nextWithType(TokenType.VALUE).text();
            tokens.nextWithText("=");
            String value = tokens.nextWithType(TokenType.VALUE).text();
            structuredValue.put(key, value);
        } while (tokens.nextWithText("}", ",").text().equals(","));
        return new Condition.Value.Structured(structuredValue);
    }
}
