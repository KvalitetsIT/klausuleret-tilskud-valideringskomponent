package dk.kvalitetsit.itukt.management.featureTests;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.Lexer;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.DslParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression.ExpressionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression.TokenParserFactory;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression.condition.ConditionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression.condition.MultiValueTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression.condition.StructuredValueTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.parser.expression.condition.builder.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Expression;

import java.util.List;

class DslParserTest {


    private static DslParser parser;

    @BeforeAll
    static void setUp() {
        StructuredValueTokenParser structuredValueTokenParser = new StructuredValueTokenParser();
        var conditionTokenParser = new ConditionTokenParser(new MultiValueTokenParser(structuredValueTokenParser), structuredValueTokenParser);
        List<ConditionBuilder> conditionBuilders = List.of(
                new AgeConditionBuilder(),
                new IndicationConditionBuilder(),
                new DoctorSpecialityConditionBuilder(),
                new DepartmentSpecialityConditionBuilder(),
                new ExistingDrugMedicationConditionBuilder()
        );
        var tokenParserFactory = new TokenParserFactory(conditionTokenParser, conditionBuilders);
        var expressionParser = new ExpressionTokenParser(tokenParserFactory);
        parser = new DslParser(expressionParser, new Lexer());
    }

    @Test
    void sampleTest() {
        String dsl = """
                    indikation = A og
                    alder = 7 eller
                    LÆGESPECIALE = hest eller
                    AFDELINGSSPECIALE i [1, 2] eller
                    EKSISTERENDE_LÆGEMIDDEL i [{atc = A01}, {atc = A02, form = tablet}]
                """;
        Expression expression = parser.parse(dsl);
    }

}