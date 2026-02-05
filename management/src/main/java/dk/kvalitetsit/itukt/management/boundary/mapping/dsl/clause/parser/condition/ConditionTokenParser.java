package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Token;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.TokenType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.DslParserException;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenIterator;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenParser;
import org.openapitools.model.Operator;

import java.util.Arrays;
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
    public Condition parse(TokenIterator tokens) {
        Identifier identifier = Identifier.from(tokens.nextWithType(TokenType.VALUE).text());
        Token operator = tokens.nextWithType(TokenType.OPERATOR);

        if (multiValueParser.canParse(tokens)) {
            List<String> values = multiValueParser.parse(tokens);
            return createMultiValueCondition(identifier, operator, values);
        } else {
            String value = tokens.nextWithType(TokenType.VALUE).text();
            return new Condition.SingleValueCondition(identifier, Operator.fromValue(operator.text()), value);
        }
    }

    @Override
    public boolean canParse(TokenIterator tokens) {
        return tokens.nextHasType(TokenType.VALUE) &&
                Arrays.stream(Identifier.values())
                        .anyMatch(id -> id.toString().equalsIgnoreCase(tokens.peek().text()));
    }

    private static Condition createMultiValueCondition(Identifier identifier, Token operator, List<String> values) {
        if (!operator.text().equalsIgnoreCase("i")) {
            throw new DslParserException("Unexpected operator for multi-value condition: " + operator.text());
        }
        return new Condition.MultiValueCondition(identifier, values);
    }

}
