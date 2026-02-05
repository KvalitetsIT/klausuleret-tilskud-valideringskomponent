package dk.kvalitetsit.itukt.management.featureTests;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Lexer;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Token;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.ConditionExpressionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenCollection;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.ConditionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.MultiValueTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.builder.AgeConditionBuilder;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Expression;

import java.util.List;

class DslParserTest {
    @Test
    void sampleTest() {
        List<Token> tokens = new Lexer("alder < 3").getTokens();

        ConditionExpressionTokenParser parser = new ConditionExpressionTokenParser(new ConditionTokenParser(new MultiValueTokenParser()), List.of(new AgeConditionBuilder()));

        Expression expression = parser.parse(new TokenCollection(tokens));
    }

}