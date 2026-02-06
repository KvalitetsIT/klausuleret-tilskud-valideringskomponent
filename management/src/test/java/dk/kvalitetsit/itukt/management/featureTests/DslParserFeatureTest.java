package dk.kvalitetsit.itukt.management.featureTests;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.Lexer;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.ExpressionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.TokenParserFactory;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.ConditionTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.MultiValueTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.StructuredValueTokenParser;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.tokenparser.condition.builder.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.model.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DslParserFeatureTest {

    private static DslParser parser;

    @BeforeAll
    static void beforeAll() {
        var structuredValueTokenParser = new StructuredValueTokenParser();
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
    void givenTwoValidDslWithAndWithoutParenthesis_whenParse_thenAssertAndHasHigherPrecedence() {

        var expected = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .operator(BinaryOperator.OR)
                .left(new IndicationCondition()
                        .type(ExpressionType.INDICATION)
                        .value("C10BA03")
                )
                .right(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .left(new BinaryExpression()
                                .type(ExpressionType.BINARY)
                                .left(new IndicationCondition()
                                        .type(ExpressionType.INDICATION)
                                        .value("C10BA02")
                                )
                                .operator(BinaryOperator.OR)
                                .right(new IndicationCondition()
                                        .type(ExpressionType.INDICATION)
                                        .value("C10BA05")
                                )
                        )
                        .operator(BinaryOperator.AND)
                        .right(new AgeCondition()
                                .type(ExpressionType.AGE)
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                .value(13)
                        )
                );

        var validDSLs = List.of(
                "(INDIKATION = C10BA03) eller (INDIKATION i [C10BA02, C10BA05]) og (ALDER >= 13)",
                "INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og ALDER >= 13",
                "INDIKATION = C10BA03 eller (INDIKATION i [C10BA02, C10BA05] og ALDER >= 13)",
                "(((INDIKATION = C10BA03) eller (((INDIKATION i [C10BA02, C10BA05] og ALDER >= 13)))))"
        );

        validDSLs.forEach(dsl -> assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl + " - The AND operator is expected to have higher precedence"));
    }


    @Test
    void givenDeeplyNestedDsl_whenParse_thenCorrectExpressionTree() {
        var expected = new BinaryExpression(
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("X", ExpressionType.INDICATION),
                                BinaryOperator.OR,
                                new IndicationCondition("Y", ExpressionType.INDICATION),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.AND,
                        new BinaryExpression(
                                new IndicationCondition("Z", ExpressionType.INDICATION),
                                BinaryOperator.OR,
                                new IndicationCondition("W", ExpressionType.INDICATION),
                                ExpressionType.BINARY
                        ),
                        ExpressionType.BINARY

                ),
                BinaryOperator.AND,
                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 10, ExpressionType.AGE),
                ExpressionType.BINARY
        );

        var subjects = List.of(
                "((INDIKATION = X eller INDIKATION = Y) og (INDIKATION = Z eller INDIKATION = W)) og (ALDER >= 10)",
                "((INDIKATION = X eller INDIKATION = Y) og (INDIKATION = Z eller INDIKATION = W)) og ALDER >= 10"
        );


        subjects.forEach(subject -> assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject));

    }

    @Test
    void givenDeeplyNestedDsl_whenParse_thenCorrectExpressionTree_2() {
        var expected = new BinaryExpression(new BinaryExpression(
                new IndicationCondition("X", ExpressionType.INDICATION),
                BinaryOperator.OR,
                new IndicationCondition("Y", ExpressionType.INDICATION),
                ExpressionType.BINARY
        ),
                BinaryOperator.AND,
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("Z", ExpressionType.INDICATION),
                                BinaryOperator.OR,
                                new IndicationCondition("W", ExpressionType.INDICATION),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.AND,
                        new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 10, ExpressionType.AGE),
                        ExpressionType.BINARY

                ),
                ExpressionType.BINARY
        );
        var subject = "(INDIKATION = X eller INDIKATION = Y) og ((INDIKATION = Z eller INDIKATION = W) og ALDER >= 10)";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithLists_whenParse_thenExpandListCorrectly() {
        var expected = new BinaryExpression(
                new BinaryExpression(
                        new IndicationCondition("C10BA01", ExpressionType.INDICATION),
                        BinaryOperator.OR,
                        new IndicationCondition("C10BA02", ExpressionType.INDICATION),
                        ExpressionType.BINARY
                ),
                BinaryOperator.OR,
                new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                ExpressionType.BINARY
        );
        var validDSLs = List.of(
                "INDIKATION i [C10BA01, C10BA02, C10BA03]",
                "(INDIKATION i [C10BA01, C10BA02]) eller INDIKATION = C10BA03",
                "((INDIKATION i [C10BA01, C10BA02]) eller INDIKATION = C10BA03)"
        );
        validDSLs.forEach(dsl -> assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl));
    }

    @Test
    void givenDslWithNumericConditions_whenParse_thenOperatorsParsedCorrectly() {
        var expected = new BinaryExpression(
                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 18, ExpressionType.AGE),
                BinaryOperator.AND,
                new AgeCondition(Operator.LESS_THAN_OR_EQUAL_TO, 65, ExpressionType.AGE),
                ExpressionType.BINARY
        );
        var subject = "ALDER >= 18 og ALDER <= 65";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenABinaryExpressionWithTwoNumericConditions_whenParse_thenOperatorsParsedCorrectly() {
        var expected = new BinaryExpression(
                new AgeCondition(Operator.GREATER_THAN, 0, ExpressionType.AGE),
                BinaryOperator.OR,
                new AgeCondition(Operator.LESS_THAN, 100, ExpressionType.AGE),
                ExpressionType.BINARY
        );
        var subject = "ALDER > 0 eller ALDER < 100";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithNumericCondition_whenParse_thenEqualIsParsedCorrectly() {
        var expected = new AgeCondition(Operator.EQUAL, 42, ExpressionType.AGE);
        var subject = "ALDER = 42";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithDepartmentSpecialityCondition_whenParse_thenItIsParsedCorrectly() {
        var subject = "AFDELINGSSPECIALE = test";

        var expression = parser.parse(subject);

        var expected = new DepartmentSpecialityCondition("TEST", ExpressionType.DEPARTMENT_SPECIALITY);
        assertEquals(expected, expression, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithSpecialityCondition_whenParse_thenItIsParsedCorrectly() {
        var expected = new DoctorSpecialityCondition("LÆGE", ExpressionType.DOCTOR_SPECIALITY);
        var subject = "LÆGESPECIALE = læge";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithOrSpecialityCondition_whenParse_thenItIsParsedCorrectly() {
        var expected = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new DoctorSpecialityCondition("LÆGE1", ExpressionType.DOCTOR_SPECIALITY))
                .operator(BinaryOperator.OR)
                .right(new DoctorSpecialityCondition("LÆGE2", ExpressionType.DOCTOR_SPECIALITY));
        var subject = "LÆGESPECIALE i [læge1, læge2]";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleExistingDrugConditions_whenParse_thenParseDrugCorrectly() {
        var expected = new BinaryExpression(
                new ExistingDrugMedicationCondition("C10B", "TABLET", "ORAL", ExpressionType.EXISTING_DRUG_MEDICATION),
                BinaryOperator.OR,
                new ExistingDrugMedicationCondition("B01AC", "INJEKTION", "INTRAVENØS", ExpressionType.EXISTING_DRUG_MEDICATION),
                ExpressionType.BINARY
        );

        var subject = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = tablet, ROUTE = oral}, {ATC = B01AC, FORM = injektion, ROUTE = intravenøs}]";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithExistingDrugConditionIncludingWildcards_whenParse_thenParseDrugCorrectly() {
        var expected = new ExistingDrugMedicationCondition("C10B", "*", "*", ExpressionType.EXISTING_DRUG_MEDICATION);
        var subject = "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = *}";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithoutParentheses_whenParse_thenAndHasHigherPrecedence() {
        var expected = new BinaryExpression(
                new IndicationCondition("X", ExpressionType.INDICATION),
                BinaryOperator.OR,
                new BinaryExpression(
                        new IndicationCondition("Y", ExpressionType.INDICATION),
                        BinaryOperator.AND,
                        new IndicationCondition("Z", ExpressionType.INDICATION),
                        ExpressionType.BINARY
                ),
                ExpressionType.BINARY);

        var subject_1 = "INDIKATION = X eller INDIKATION = Y og INDIKATION = Z";
        var subject_2 = "INDIKATION = X eller (INDIKATION = Y og INDIKATION= Z)";

        assertEquals(expected, parser.parse(subject_1), "Unexpected mapping of: " + subject_1);
        assertEquals(expected, parser.parse(subject_2), "Unexpected mapping of: " + subject_2);
    }

    @Test
    void givenDslWithParentheses_whenParse_thenParenthesesHaveHigherPrecedence() {
        var expected = new BinaryExpression(
                new BinaryExpression(
                        new IndicationCondition("X", ExpressionType.INDICATION),
                        BinaryOperator.OR,
                        new IndicationCondition("Y", ExpressionType.INDICATION),
                        ExpressionType.BINARY
                ), BinaryOperator.AND,
                new IndicationCondition("Z", ExpressionType.INDICATION),
                ExpressionType.BINARY);

        final var subject = "(INDIKATION = X eller INDIKATION = Y) og INDIKATION = Z";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleBinaryOperationsAndAndWhitespace_whenParse_thenParseCorrectly() {
        var expected = new BinaryExpression(
                new BinaryExpression(
                        new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                        BinaryOperator.AND,
                        new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, ExpressionType.AGE),
                        ExpressionType.BINARY
                ),
                BinaryOperator.OR,
                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                ExpressionType.BINARY);

        final var subject = "INDIKATION =          C10BA03    OG ALDER >= 13 eLLer ALDER = 10";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleBinaryOperationsAndAllLowercase_whenParse_thenParseCorrectly() {
        var expected = new BinaryExpression(
                new BinaryExpression(
                        new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                        BinaryOperator.AND,
                        new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, ExpressionType.AGE),
                        ExpressionType.BINARY
                ),
                BinaryOperator.OR,
                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                ExpressionType.BINARY);

        final var subject = "INDIKATION = c10ba03 og ALDER >= 13 eller ALDER = 10";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithMultipleBinaryOperationsAndRandomCasing_whenParse_thenParseCorrectly() {
        var expected = new BinaryExpression(
                new BinaryExpression(
                        new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                        BinaryOperator.AND,
                        new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, ExpressionType.AGE),
                        ExpressionType.BINARY
                ),
                BinaryOperator.OR,
                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                ExpressionType.BINARY);

        final var subject = "indiKaTion = C10BA03 OG ALDER >= 13 ELLER aLder = 10";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleOrOperations_whenParse_thenParseCorrectly() {
        var expected = new BinaryExpression(
                new BinaryExpression(
                        new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, ExpressionType.AGE),
                        ExpressionType.BINARY
                ),
                BinaryOperator.OR,
                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                ExpressionType.BINARY);

        final var subject = "INDIKATION = C10BA03 eller ALDER >= 13 eller ALDER = 10";
        assertEquals(expected, parser.parse(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenSeveralValidDslWithParenthesesPositionedDifferently_whenParse_thenReturnSameClause() {

        var expected = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .operator(BinaryOperator.OR)
                .left(new IndicationCondition()
                        .type(ExpressionType.INDICATION)
                        .value("C10BA03")
                )
                .right(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .left(new BinaryExpression()
                                .type(ExpressionType.BINARY)
                                .left(new IndicationCondition()
                                        .type(ExpressionType.INDICATION)
                                        .value("C10BA02")
                                )
                                .operator(BinaryOperator.OR)
                                .right(new IndicationCondition()
                                        .type(ExpressionType.INDICATION)
                                        .value("C10BA05")
                                )
                        )
                        .operator(BinaryOperator.AND)
                        .right(new AgeCondition()
                                .type(ExpressionType.AGE)
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                .value(13)
                        )
                );

        var dsls = List.of(
                "(INDIKATION = C10BA03) eller (INDIKATION i [C10BA02, C10BA05]) og (ALDER >= 13)",
                "(INDIKATION = C10BA03) eller ((INDIKATION i [C10BA02, C10BA05]) og (ALDER >= 13))",
                "((INDIKATION = C10BA03) eller ((INDIKATION i [C10BA02, C10BA05]) og (ALDER >= 13)))",
                "INDIKATION = C10BA03 eller (INDIKATION i [C10BA02, C10BA05] og ALDER >= 13)",
                "INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og ALDER >= 13"
        );

        dsls.forEach(dsl -> assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl));
    }


    @Test
    void givenAnExistingDrugMedicationDsL_whenParse_thenIgnoreOrderOfFields() {
        var expected = new BinaryExpression(
                new ExistingDrugMedicationCondition("C10B", "TABLET", "ORAL", ExpressionType.EXISTING_DRUG_MEDICATION),
                BinaryOperator.OR,
                new ExistingDrugMedicationCondition("B01AC", "INJEKTION", "INTRAVENØS",
                        ExpressionType.EXISTING_DRUG_MEDICATION),
                ExpressionType.BINARY
        );

        var dsls = List.of(
                "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}]",
                "EKSISTERENDE_LÆGEMIDDEL i [{FORM = TABLET, ATC = C10B, ROUTE = ORAL}, {ROUTE = INTRAVENØS, ATC = B01AC, FORM = INJEKTION}]",
                "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B,FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, ROUTE = INTRAVENØS, FORM = INJEKTION}]",
                "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, ROUTE = ORAL, FORM = TABLET}, {ATC = B01AC, ROUTE = INTRAVENØS, FORM = INJEKTION}]"
        );

        dsls.forEach(dsl -> assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl));
    }


    @Test
    void givenAnExistingDrugMedicationDsLWithWildcardFields_whenParse_thenMapCorrectly() {
        var cases = Map.of(
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = ORAL}",
                new ExistingDrugMedicationCondition(
                        "C10B",
                        "TABLET",
                        "ORAL",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = *}",
                new ExistingDrugMedicationCondition(
                        "C10B",
                        "TABLET",
                        "*",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = ORAL}",
                new ExistingDrugMedicationCondition(
                        "C10B",
                        "*",
                        "ORAL",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE = ORAL}",
                new ExistingDrugMedicationCondition(
                        "*",
                        "TABLET",
                        "ORAL",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),

                "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = *}",
                new ExistingDrugMedicationCondition(
                        "C10B",
                        "*",
                        "*",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = *, ROUTE =  ORAL}",
                new ExistingDrugMedicationCondition(
                        "*",
                        "*",
                        "ORAL",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE =  *}",
                new ExistingDrugMedicationCondition(
                        "*",
                        "TABLET",
                        "*",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = *, ROUTE =  *}",
                new ExistingDrugMedicationCondition(
                        "*",
                        "*",
                        "*",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                )
        );

        cases.forEach((dsl, expected) -> assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl));
    }


    @Test
    void givenAnExistingDrugMedicationDsLWithIgnoredFields_whenParse_thenMapCorrectly() {
        var cases = Map.of(
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = ORAL}",
                new ExistingDrugMedicationCondition(
                        "C10B",
                        "TABLET",
                        "ORAL",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET}",
                new ExistingDrugMedicationCondition(
                        "C10B",
                        "TABLET",
                        "*",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, ROUTE = ORAL}",
                new ExistingDrugMedicationCondition(
                        "C10B",
                        "*",
                        "ORAL",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {FORM = TABLET, ROUTE = ORAL}",
                new ExistingDrugMedicationCondition(
                        "*",
                        "TABLET",
                        "ORAL",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),

                "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B}",
                new ExistingDrugMedicationCondition(
                        "C10B",
                        "*",
                        "*",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {ROUTE =  ORAL}",
                new ExistingDrugMedicationCondition(
                        "*",
                        "*",
                        "ORAL",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                ),
                "EKSISTERENDE_LÆGEMIDDEL = {FORM = TABLET}",
                new ExistingDrugMedicationCondition(
                        "*",
                        "TABLET",
                        "*",
                        ExpressionType.EXISTING_DRUG_MEDICATION
                )
        );

        cases.forEach((dsl, expected) -> assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl));
    }


    @Test
    void givenADsLWithMultipleMergedAgeConditions_whenParse_thenCorrectlySetOperatorToEquals() {

        var expected = new BinaryExpression(
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.EQUAL, 20, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 30, ExpressionType.AGE),
                        ExpressionType.BINARY
                );

        var dsl = "ALDER i [10, 20, 30]";

        assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl);
    }


    @Test
    void givenADsLWithMultipleAgeConditionsWithVaryingOperators_whenParse_thenMapCorrectly() {

        var expected = new BinaryExpression(
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 20, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.LESS_THAN, 30, ExpressionType.AGE),
                        ExpressionType.BINARY
                );

        var dsl = "ALDER = 10 ELLER ALDER >= 20 ELLER ALDER < 30";

        assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl);
    }


    @Test
    void givenADsLWithMultipleAgeConditionsWithVaryingOperators_whenParse_thenHandlePrecedenceCorrectly() {

        var expected = new BinaryExpression(
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 20, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.AND,
                        new AgeCondition(Operator.EQUAL, 30, ExpressionType.AGE),
                        ExpressionType.BINARY
                );

        var dsl = "(ALDER = 10 ELLER ALDER >= 20) og ALDER = 30";

        assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl);

    }

    @Test
    void givenADsLWithMultipleAgeConditions_whenParse_thenHandlePrecedenceCorrectly() {

        var expected = new BinaryExpression(
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.EQUAL, 20, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.AND,
                        new AgeCondition(Operator.EQUAL, 30, ExpressionType.AGE),
                        ExpressionType.BINARY
                );

        var dsl = "(ALDER i [10, 20]) og ALDER = 30";

        assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl);

    }

    @Test
    void givenADsLWithMultipleNonMergedAgeConditions_whenParse_thenMapCorrectly() {

        var expected = new BinaryExpression(
                        new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                        BinaryOperator.OR,
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 20, ExpressionType.AGE),
                                BinaryOperator.AND,
                                new AgeCondition(Operator.EQUAL, 30, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        ExpressionType.BINARY
                );

        var dsl = "ALDER = 10 eller alder = 20 og ALDER = 30";

        assertEquals(expected, parser.parse(dsl), "Unexpected mapping of: " + dsl);

    }

    @Test
    void givenDSLWithInvalidUsageOfMultipleValues_whenParse_thenThrowError() {
        var dsl = "ALDER i 10, 20, 30";
        Assertions.assertThrows(
                RuntimeException.class,
                () -> parser.parse(dsl),
                "If a dsl with an invalid array is given an exception is expected to be thrown");
    }


}