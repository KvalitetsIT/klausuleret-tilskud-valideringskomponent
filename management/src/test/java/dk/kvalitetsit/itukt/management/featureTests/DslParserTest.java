package dk.kvalitetsit.itukt.management.featureTests;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Lexer;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.DslParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.ExpressionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.TokenParserFactory;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.ConditionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.MultiValueTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.builder.AgeConditionBuilder;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.builder.ConditionBuilder;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.builder.DoctorSpecialityConditionBuilder;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.condition.builder.IndicationConditionBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Expression;

import java.util.List;

class DslParserTest {


    private static DslParser parser;

    @BeforeAll
    static void setUp() {
        var conditionTokenParser = new ConditionTokenParser(new MultiValueTokenParser());
        List<ConditionBuilder> conditionBuilders = List.of(
                new AgeConditionBuilder(),
                new IndicationConditionBuilder(),
                new DoctorSpecialityConditionBuilder()
        );
        var tokenParserFactory = new TokenParserFactory(conditionTokenParser, conditionBuilders);
        var expressionParser = new ExpressionTokenParser(tokenParserFactory);
        parser = new DslParser(expressionParser, new Lexer());
    }

    @Test
    void sampleTest() {
        Expression expression = parser.parse("indikation = A og alder = 7 eller LÆGESPECIALE = hest");
    }

}