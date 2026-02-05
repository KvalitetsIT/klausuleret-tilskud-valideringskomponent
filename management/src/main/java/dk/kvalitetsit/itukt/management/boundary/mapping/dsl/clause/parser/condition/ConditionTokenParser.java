package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Token;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.TokenType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenCollection;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenParser;
import org.openapitools.model.Operator;

import java.util.List;

/**
 * Parses a condition, consisting of an identifier, an operator, and one or more values.
 * E.g. "alder > 30" or "indikation i [A, B, C]"
 */
public class ConditionTokenParser implements TokenParser<Condition> {
    private final MultiValueTokenParser multiValueParser;

    public ConditionTokenParser(MultiValueTokenParser multiValueParser) {
        this.multiValueParser = multiValueParser;
    }

    @Override
    public Condition parse(TokenCollection tokens) {
        TokenIterator iterator = tokens.iterator();
        Identifier identifier = Identifier.from(iterator.nextWithType(TokenType.VALUE).text());
        Token operator = iterator.nextWithType(TokenType.OPERATOR);

        var valueTokens = tokens.tokensAfter(operator);
        return multiValueParser.tryParse(valueTokens)
                .map(values -> createMultiValueCondition(identifier, operator, values))
                .orElseGet(() -> createSingleValueCondition(identifier, operator, valueTokens));

    }

    private static Condition createSingleValueCondition(Identifier identifier, Token operator, TokenCollection valueTokens) {
        var valuesIterator = valueTokens.iterator();
        String value = valuesIterator.nextWithType(TokenType.VALUE).text();
        valuesIterator.expectNoMoreTokens();
        return new Condition.SingleValueCondition(identifier, Operator.fromValue(operator.text()), value);
    }

    private static Condition createMultiValueCondition(Identifier identifier, Token operator, List<String> values) {
        if (!operator.text().equalsIgnoreCase("i")) {
            throw new DslParserException("Unexpected operator for multi-value condition: " + operator.text());
        }
        return new Condition.MultiValueCondition(identifier, values);
    }

}
