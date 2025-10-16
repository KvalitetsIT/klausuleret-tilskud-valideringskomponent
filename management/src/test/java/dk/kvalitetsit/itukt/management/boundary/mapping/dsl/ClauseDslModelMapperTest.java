package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;
import org.openapitools.model.Error;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ClauseDslModelMapperTest {


    @InjectMocks
    private ClauseDslModelMapper mapper;

    @Test
    void givenTwoValidDslWithAndWithoutParenthesis_whenMap_thenAssertAndHasHigherPrecedence() {

        final var expected = new ClauseInput("CLAUSE", new BinaryExpression()
                .type("BinaryExpression")
                .operator(BinaryOperator.OR)
                .left(new IndicationCondition()
                        .type("StringCondition")
                        .value("C10BA03")
                )
                .right(new BinaryExpression()
                        .type("BinaryExpression")
                        .left(new BinaryExpression()
                                .type("BinaryExpression")
                                .left(new IndicationCondition()
                                        .type("StringCondition")
                                        .value("C10BA02")
                                )
                                .operator(BinaryOperator.OR)
                                .right(new IndicationCondition()
                                        .type("StringCondition")
                                        .value("C10BA05")
                                )
                        )
                        .operator(BinaryOperator.AND)
                        .right(new AgeCondition()
                                .type("NumberCondition")
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                .value(13)
                        )
                ),
                new Error("blaah")
        );

        List<DslInput> validDSLs = Stream.of(
                        "Klausul CLAUSE: (INDICATION = C10BA03) eller (INDICATION i C10BA02, C10BA05) og (AGE >= 13)",
                        "Klausul CLAUSE: INDICATION = C10BA03 eller INDICATION i C10BA02, C10BA05 og AGE >= 13",
                        "Klausul CLAUSE: INDICATION = C10BA03 eller (INDICATION i C10BA02, C10BA05 og AGE >= 13)",
                        "Klausul CLAUSE: (((INDICATION = C10BA03) eller (((INDICATION i C10BA02, C10BA05 og AGE >= 13)))))"
                )
                .map(x -> new DslInput(new Error("blaah"), x))
                .toList();

        validDSLs.forEach(dsl -> Assertions.assertEquals(mapper.map(dsl), expected, "Unexpected mapping of: " + dsl + " - The AND operator is expected to have higher precedence"));
    }


    @Test
    void givenDeeplyNestedDsl_whenMap_thenCorrectExpressionTree() {
        final ClauseInput expected = new ClauseInput("DEEP",
                new BinaryExpression(
                        new BinaryExpression(
                                new BinaryExpression(
                                        new IndicationCondition("X", "StringCondition"),
                                        BinaryOperator.OR,
                                        new IndicationCondition("Y", "StringCondition"),
                                        "BinaryExpression"
                                ),
                                BinaryOperator.AND,
                                new BinaryExpression(
                                        new IndicationCondition("Z", "StringCondition"),
                                        BinaryOperator.OR,
                                        new IndicationCondition("W", "StringCondition"),
                                        "BinaryExpression"
                                ),
                                "BinaryExpression"

                        ),
                        BinaryOperator.AND,
                        new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 10, "NumberCondition"),
                        "BinaryExpression"
                ), new Error("blaah")
        );

        List<DslInput> subjects = Stream.of(
                        "Klausul DEEP: ((INDICATION = X eller INDICATION = Y) og (INDICATION = Z eller INDICATION = W)) og (AGE >= 10)",
                        "Klausul DEEP: ((INDICATION = X eller INDICATION = Y) og (INDICATION = Z eller INDICATION = W)) og AGE >= 10"
                )
                .map(x -> new DslInput(new Error("blaah"), x))
                .toList();
        ;

        subjects.forEach(subject -> Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject));

    }

    @Test
    void givenDeeplyNestedDsl_whenMap_thenCorrectExpressionTree_2() {
        final ClauseInput expected = new ClauseInput("DEEP",
                new BinaryExpression(new BinaryExpression(
                        new IndicationCondition("X", "StringCondition"),
                        BinaryOperator.OR,
                        new IndicationCondition("Y", "StringCondition"),
                        "BinaryExpression"
                ),
                        BinaryOperator.AND,
                        new BinaryExpression(
                                new BinaryExpression(
                                        new IndicationCondition("Z", "StringCondition"),
                                        BinaryOperator.OR,
                                        new IndicationCondition("W", "StringCondition"),
                                        "BinaryExpression"
                                ),
                                BinaryOperator.AND,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 10, "NumberCondition"),
                                "BinaryExpression"

                        ),
                        "BinaryExpression"
                ),
                new Error("blaah")
        );
        DslInput subject = new DslInput(new Error("blaah"), "Klausul DEEP: (INDICATION = X eller INDICATION = Y) og ((INDICATION = Z eller INDICATION = W) og AGE >= 10)");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithLists_whenMap_thenExpandListCorrectly() {
        final ClauseInput expected = new ClauseInput(
                "LIST",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA01", "StringCondition"),
                                BinaryOperator.OR,
                                new IndicationCondition("C10BA02", "StringCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new IndicationCondition("C10BA03", "StringCondition"),
                        "BinaryExpression"
                ),
                new Error("blaah")
        );
        List<DslInput> validDSLs = Stream.of(
                "Klausul LIST: INDICATION i C10BA01, C10BA02, C10BA03",
                "Klausul LIST: (INDICATION i C10BA01, C10BA02) eller INDICATION = C10BA03",
                "Klausul LIST: ((INDICATION i C10BA01, C10BA02) eller INDICATION = C10BA03)"
        ).map(x -> new DslInput(new Error("blaah"), x)).toList();
        validDSLs.forEach(dsl -> Assertions.assertEquals(expected, mapper.map(dsl), "Unexpected mapping of: " + dsl));
    }

    @Test
    void givenDslWithNumericConditions_whenMap_thenOperatorsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE", new BinaryExpression(
                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 18, "NumberCondition"),
                BinaryOperator.AND,
                new AgeCondition(Operator.LESS_THAN_OR_EQUAL_TO, 65, "NumberCondition"),
                "BinaryExpression"
        ), new Error("blaah"));
        DslInput subject = new DslInput(new Error("blaah"), "Klausul CLAUSE: AGE >= 18 og AGE <= 65");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenABinaryExpressionWithTwoNumericConditions_whenMap_thenOperatorsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput(
                "CLAUSE",
                new BinaryExpression(
                        new AgeCondition(Operator.GREATER_THAN, 0, "NumberCondition"),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.LESS_THAN, 100, "NumberCondition"),
                        "BinaryExpression"
                ),
                new Error("blaah")
        );
        DslInput subject = new DslInput(new Error("blaah"), "Klausul CLAUSE: AGE > 0 eller AGE < 100");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithNumericCondition_whenMap_thenEqualIsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE", new AgeCondition(Operator.EQUAL, 42, "NumberCondition"),
                new Error("blaah"));
        DslInput subject = new DslInput(new Error("blaah"), "Klausul CLAUSE: AGE = 42");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleExistingDrugConditions_whenMap_thenParseDrugCorrectly() {
        final ClauseInput expected = new ClauseInput("DRUG", new BinaryExpression(
                new ExistingDrugMedicationCondition("C10B", "TABLET", "ORAL", "ExistingDrugMedicationCondition"),
                BinaryOperator.OR,
                new ExistingDrugMedicationCondition("B01AC", "INJEKTION", "INTRAVENØS", "ExistingDrugMedicationCondition"),
                "BinaryExpression"
        ), new Error("blaah")
        );

        DslInput subject = new DslInput(new Error("blaah"), "Klausul DRUG: EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = tablet, ROUTE = oral}, {ATC = B01AC, FORM = injektion, ROUTE = intravenøs}]");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithExistingDrugConditionIncludingWildcards_whenMap_thenParseDrugCorrectly() {
        final ClauseInput expected = new ClauseInput("DRUG", new ExistingDrugMedicationCondition("C10B", "*", "*", "ExistingDrugMedicationCondition"),
                new Error("blaah"));
        DslInput subject = new DslInput(new Error("blaah"), "Klausul DRUG: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = *}");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithoutParentheses_whenMap_thenAndHasHigherPrecedence() {
        final ClauseInput expected = new ClauseInput("PRECEDENCE",
                new BinaryExpression(
                        new IndicationCondition("X", "StringCondition"),
                        BinaryOperator.OR,
                        new BinaryExpression(
                                new IndicationCondition("Y", "StringCondition"),
                                BinaryOperator.AND,
                                new IndicationCondition("Z", "StringCondition"),
                                "BinaryExpression"
                        ),
                        "BinaryExpression"),
                new Error("blaah")
        );

        DslInput subject_1 = new DslInput(new Error("blaah"), "Klausul PRECEDENCE: INDICATION = X eller INDICATION = Y og INDICATION = Z");
        DslInput subject_2 = new DslInput(new Error("blaah"), "Klausul PRECEDENCE: INDICATION = X eller (INDICATION = Y og INDICATION= Z)");

        Assertions.assertEquals(expected, mapper.map(subject_1), "Unexpected mapping of: " + subject_1);
        Assertions.assertEquals(expected, mapper.map(subject_2), "Unexpected mapping of: " + subject_2);
    }

    @Test
    void givenDslWithParentheses_whenMap_thenParenthesesHaveHigherPrecedence() {
        final ClauseInput expected = new ClauseInput("PRECEDENCE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("X", "StringCondition"),
                                BinaryOperator.OR,
                                new IndicationCondition("Y", "StringCondition"),
                                "BinaryExpression"
                        ), BinaryOperator.AND,
                        new IndicationCondition("Z", "StringCondition"),
                        "BinaryExpression"),
                new Error("blaah")

        );

        final DslInput subject = new DslInput(new Error("blaah"), "Klausul PRECEDENCE: (INDICATION = X eller INDICATION = Y) og INDICATION = Z");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleBinaryOperationsAndAndWhitespace_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA03", "StringCondition"),
                                BinaryOperator.AND,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, "NumberCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 10, "NumberCondition"),
                        "BinaryExpression"),
                new Error("blaah")
        );

        final DslInput subject = new DslInput(new Error("blaah"), "Klausul CLAUSE: INDICATION =          C10BA03    OG AGE >= 13 eLLer AGE = 10");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleBinaryOperationsAndAllLowercase_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA03", "StringCondition"),
                                BinaryOperator.AND,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, "NumberCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 10, "NumberCondition"),
                        "BinaryExpression"),
                new Error("blaah")
        );

        final DslInput subject = new DslInput(new Error("blaah"), "klausul clause: indication = c10ba03 og age >= 13 eller age = 10");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithMultipleBinaryOperationsAndRandomCasing_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA03", "StringCondition"),
                                BinaryOperator.AND,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, "NumberCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 10, "NumberCondition"),
                        "BinaryExpression"),
                new Error("blaah")
        );

        final DslInput subject = new DslInput(new Error("blaah"), "kLausul CLAUSE: indiCaTion = C10BA03 OG age >= 13 ELLER aGe = 10)");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleOrOperations_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA03", "StringCondition"),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, "NumberCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 10, "NumberCondition"),
                        "BinaryExpression"),
                new Error("blaah")
        );

        final DslInput subject = new DslInput(new Error("blaah"), "Klausul CLAUSE: INDICATION = C10BA03 eller AGE >= 13 eller AGE = 10");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenSeveralValidDslWithParenthesesPositionedDifferently_whenMap_thenReturnSameClause() {

        var expected = new ClauseInput("CLAUSE", new BinaryExpression()
                .type("BinaryExpression")
                .operator(BinaryOperator.OR)
                .left(new IndicationCondition()
                        .type("StringCondition")
                        .value("C10BA03")
                )
                .right(new BinaryExpression()
                        .type("BinaryExpression")
                        .left(new BinaryExpression()
                                .type("BinaryExpression")
                                .left(new IndicationCondition()
                                        .type("StringCondition")
                                        .value("C10BA02")
                                )
                                .operator(BinaryOperator.OR)
                                .right(new IndicationCondition()
                                        .type("StringCondition")
                                        .value("C10BA05")
                                )
                        )
                        .operator(BinaryOperator.AND)
                        .right(new AgeCondition()
                                .type("NumberCondition")
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                .value(13)
                        )
                ),
                new Error("blaah")
        );

        var dsls = Stream.of(
                "Klausul CLAUSE: (INDICATION = C10BA03) eller (INDICATION i C10BA02, C10BA05) og (AGE >= 13)",
                "Klausul CLAUSE: (INDICATION = C10BA03) eller ((INDICATION i C10BA02, C10BA05) og (AGE >= 13))",
                "Klausul CLAUSE: ((INDICATION = C10BA03) eller ((INDICATION i C10BA02, C10BA05) og (AGE >= 13)))",
                "Klausul CLAUSE: INDICATION = C10BA03 eller (INDICATION i C10BA02, C10BA05 og AGE >= 13)",
                "Klausul CLAUSE: INDICATION = C10BA03 eller INDICATION i C10BA02, C10BA05 og AGE >= 13"
        ).map(x -> new DslInput(new Error("blaah"), x)).toList();

        dsls.forEach(dsl -> Assertions.assertEquals(expected, mapper.map(dsl), "Unexpected mapping of: " + dsl));
    }
}