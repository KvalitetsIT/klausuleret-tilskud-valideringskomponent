package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ClauseDslDtoMapperTest {


    @InjectMocks
    private ClauseDslDtoMapper mapper;

    @Test
    void givenTwoValidDslWithAndWithoutParenthesis_whenMap_thenAssertAndHasHigherPrecedence() {

        final var expected = new ClauseInput("CLAUSE", new BinaryExpression()
                .type("BinaryExpression")
                .operator(BinaryOperator.OR)
                .left(new StringCondition()
                        .type("StringCondition")
                        .field("INDICATION")
                        .value("C10BA03")
                )
                .right(new BinaryExpression()
                        .type("BinaryExpression")
                        .left(new BinaryExpression()
                                .type("BinaryExpression")
                                .left(new StringCondition()
                                        .type("StringCondition")
                                        .field("INDICATION")
                                        .value("C10BA02")
                                )
                                .operator(BinaryOperator.OR)
                                .right(new StringCondition()
                                        .type("StringCondition")
                                        .field("INDICATION")
                                        .value("C10BA05")
                                )
                        )
                        .operator(BinaryOperator.AND)
                        .right(new NumberCondition()
                                .type("NumberCondition")
                                .field("AGE")
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                .value(13)
                        )
                )
        );

        List<String> validDSLs = List.of(
                "Klausul CLAUSE: (INDICATION = C10BA03) eller (INDICATION i C10BA02, C10BA05) og (AGE >= 13)",
                "Klausul CLAUSE: INDICATION = C10BA03 eller INDICATION i C10BA02, C10BA05 og AGE >= 13",
                "Klausul CLAUSE: INDICATION = C10BA03 eller (INDICATION i C10BA02, C10BA05 og AGE >= 13)",
                "Klausul CLAUSE: (((INDICATION = C10BA03) eller (((INDICATION i C10BA02, C10BA05 og AGE >= 13)))))"
        );

        validDSLs.forEach(dsl -> Assertions.assertEquals(mapper.map(dsl), expected, "Unexpected mapping of: " + dsl + " - The AND operator is expected to have higher precedence"));
    }


    @Test
    void givenDeeplyNestedDsl_whenMap_thenCorrectExpressionTree() {
        final ClauseInput expected = new ClauseInput("DEEP",
                new BinaryExpression(
                        new BinaryExpression(
                                new BinaryExpression(
                                        new StringCondition("A", "X", "StringCondition"),
                                        BinaryOperator.OR,
                                        new StringCondition("B", "Y", "StringCondition"),
                                        "BinaryExpression"
                                ),
                                BinaryOperator.AND,
                                new BinaryExpression(
                                        new StringCondition("C", "Z", "StringCondition"),
                                        BinaryOperator.OR,
                                        new StringCondition("D", "W", "StringCondition"),
                                        "BinaryExpression"
                                ),
                                "BinaryExpression"

                        ),
                        BinaryOperator.AND,
                        new NumberCondition("E", Operator.GREATER_THAN_OR_EQUAL_TO, 10, "NumberCondition"),
                        "BinaryExpression"
                )
        );

        List<String> subjects = List.of(
                "Klausul DEEP: ((A = X eller B = Y) og (C = Z eller D = W)) og (E >= 10)",
                "Klausul DEEP: ((A = X eller B = Y) og (C = Z eller D = W)) og E >= 10"
        );

        subjects.forEach(subject -> Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject));

    }

    @Test
    void givenDeeplyNestedDsl_whenMap_thenCorrectExpressionTree_2() {
        final ClauseInput expected = new ClauseInput("DEEP",
                new BinaryExpression(new BinaryExpression(
                        new StringCondition("A", "X", "StringCondition"),
                        BinaryOperator.OR,
                        new StringCondition("B", "Y", "StringCondition"),
                        "BinaryExpression"
                ),
                        BinaryOperator.AND,
                        new BinaryExpression(
                                new BinaryExpression(
                                        new StringCondition("C", "Z", "StringCondition"),
                                        BinaryOperator.OR,
                                        new StringCondition("D", "W", "StringCondition"),
                                        "BinaryExpression"
                                ),
                                BinaryOperator.AND,
                                new NumberCondition("E", Operator.GREATER_THAN_OR_EQUAL_TO, 10, "NumberCondition"),
                                "BinaryExpression"

                        ),
                        "BinaryExpression"
                )
        );
        String subject = "Klausul DEEP: (A = X eller B = Y) og ((C = Z eller D = W) og E >= 10)";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithLists_whenMap_thenExpandListCorrectly() {
        final ClauseInput expected = new ClauseInput(
                "LIST",
                new BinaryExpression(
                        new BinaryExpression(
                                new StringCondition("INDICATION", "C10BA01", "StringCondition"),
                                BinaryOperator.OR,
                                new StringCondition("INDICATION", "C10BA02", "StringCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new StringCondition("INDICATION", "C10BA03", "StringCondition"),
                        "BinaryExpression"
                )
        );
        List<String> validDSLs = List.of(
                "Klausul LIST: INDICATION i C10BA01, C10BA02, C10BA03",
                "Klausul LIST: (INDICATION i C10BA01, C10BA02) eller INDICATION = C10BA03",
                "Klausul LIST: ((INDICATION i C10BA01, C10BA02) eller INDICATION = C10BA03)"
        );
        validDSLs.forEach(dsl -> Assertions.assertEquals(expected, mapper.map(dsl), "Unexpected mapping of: " + dsl));
    }

    @Test
    void givenDslWithNumericConditions_whenMap_thenOperatorsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE", new BinaryExpression(
                new NumberCondition("AGE", Operator.GREATER_THAN_OR_EQUAL_TO, 18, "NumberCondition"),
                BinaryOperator.AND,
                new NumberCondition("AGE", Operator.LESS_THAN_OR_EQUAL_TO, 65, "NumberCondition"),
                "BinaryExpression"
        ));
        String subject = "Klausul CLAUSE: AGE >= 18 og AGE <= 65";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenABinaryExpressionWithTwoNumericConditions_whenMap_thenOperatorsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput(
                "CLAUSE",
                new BinaryExpression(
                        new NumberCondition("AGE", Operator.GREATER_THAN, 0, "NumberCondition"),
                        BinaryOperator.OR,
                        new NumberCondition("AGE", Operator.LESS_THAN, 100, "NumberCondition"),
                        "BinaryExpression"
                )
        );
        String subject = "Klausul CLAUSE: AGE > 0 eller AGE < 100";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithNumericCondition_whenMap_thenEqualIsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE", new NumberCondition("AGE", Operator.EQUAL, 42, "NumberCondition"));
        String subject = "Klausul CLAUSE: AGE = 42";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Disabled("Awaits 'IUAKT-106: Udvid dsl til at håndtere tidligere medicinsk behandling'")
    @Test
    void givenDslWithDrugCondition_whenMap_thenParseDrugCorrectly() {
        final ClauseInput expected = new ClauseInput("DRUG", new ExistingDrugMedicationCondition("C10B", "tablet", "oral", "ExistingDrugMedicationCondition"));
        String subject = "Klausul DRUG: EXISTING_DRUG_MEDICATION (ATC = C10B, FORM = tablet, ROUTE = oral)";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Disabled("Awaits 'IUAKT-106: Udvid dsl til at håndtere tidligere medicinsk behandling'")
    @Test
    void givenDslWithDrugConditionInlcudingWildcards_whenMap_thenParseDrugCorrectly() {
        final ClauseInput expected = new ClauseInput("DRUG", new ExistingDrugMedicationCondition("C10B", "*", "*", "ExistingDrugMedicationCondition"));
        String subject = "Klausul DRUG: EXISTING_DRUG_MEDICATION (ATC = B01AC, FORM = *, ROUTE = *)";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithoutParentheses_whenMap_thenAndHasHigherPrecedence() {
        final ClauseInput expected = new ClauseInput("PRECEDENCE",
                new BinaryExpression(
                        new StringCondition("A", "X", "StringCondition"),
                        BinaryOperator.OR,
                        new BinaryExpression(
                                new StringCondition("B", "Y", "StringCondition"),
                                BinaryOperator.AND,
                                new StringCondition("C", "Z", "StringCondition"),
                                "BinaryExpression"
                        ),
                        "BinaryExpression")
        );

        final String subject_1 = "Klausul PRECEDENCE: A = X eller B = Y og C = Z";
        final String subject_2 = "Klausul PRECEDENCE: A = X eller (B = Y og C = Z)";

        Assertions.assertEquals(expected, mapper.map(subject_1), "Unexpected mapping of: " + subject_1);
        Assertions.assertEquals(expected, mapper.map(subject_2), "Unexpected mapping of: " + subject_2);
    }

    @Test
    void givenDslWithParentheses_whenMap_thenParenthesesHaveHigherPrecedence() {
        final ClauseInput expected = new ClauseInput("PRECEDENCE",
                new BinaryExpression(
                        new BinaryExpression(
                                new StringCondition("A", "X", "StringCondition"),
                                BinaryOperator.OR,
                                new StringCondition("B", "Y", "StringCondition"),
                                "BinaryExpression"
                        ), BinaryOperator.AND,
                        new StringCondition("C", "Z", "StringCondition"),
                        "BinaryExpression")

        );

        final String subject = "Klausul PRECEDENCE: (A = X eller B = Y) og C = Z";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleBinaryOperationsAndAndWhitespace_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new StringCondition("INDICATION", "C10BA03", "StringCondition"),
                                BinaryOperator.AND,
                                new NumberCondition("AGE", Operator.GREATER_THAN_OR_EQUAL_TO, 13, "NumberCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new NumberCondition("AGE", Operator.EQUAL, 10, "NumberCondition"),
                        "BinaryExpression")
        );

        final String subject = "Klausul CLAUSE: INDICATION =          C10BA03    OG AGE >= 13 eLLer AGE = 10";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleBinaryOperationsAndAllLowercase_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new StringCondition("INDICATION", "C10BA03", "StringCondition"),
                                BinaryOperator.AND,
                                new NumberCondition("AGE", Operator.GREATER_THAN_OR_EQUAL_TO, 13, "NumberCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new NumberCondition("AGE", Operator.EQUAL, 10, "NumberCondition"),
                        "BinaryExpression")
        );

        final String subject = "klausul clause: indication = c10ba03 og age >= 13 eller age = 10";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithMultipleBinaryOperationsAndRandomCasing_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new StringCondition("INDICATION", "C10BA03", "StringCondition"),
                                BinaryOperator.AND,
                                new NumberCondition("AGE", Operator.GREATER_THAN_OR_EQUAL_TO, 13, "NumberCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new NumberCondition("AGE", Operator.EQUAL, 10, "NumberCondition"),
                        "BinaryExpression")
        );

        final String subject = "kLausul CLAUSE: indiCaTion = C10BA03 OG age >= 13 ELLER aGe = 10";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleOrOperations_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new StringCondition("INDICATION", "C10BA03", "StringCondition"),
                                BinaryOperator.OR,
                                new NumberCondition("AGE", Operator.GREATER_THAN_OR_EQUAL_TO, 13, "NumberCondition"),
                                "BinaryExpression"
                        ),
                        BinaryOperator.OR,
                        new NumberCondition("AGE", Operator.EQUAL, 10, "NumberCondition"),
                        "BinaryExpression")
        );

        final String subject = "Klausul CLAUSE: INDICATION = C10BA03 eller AGE >= 13 eller AGE = 10";
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenSeveralValidDslWithParenthesesPositionedDifferently_whenMap_thenReturnSameClause() {

        var expected = new ClauseInput("CLAUSE", new BinaryExpression()
                .type("BinaryExpression")
                .operator(BinaryOperator.OR)
                .left(new StringCondition()
                        .type("StringCondition")
                        .field("INDICATION")
                        .value("C10BA03")
                )
                .right(new BinaryExpression()
                        .type("BinaryExpression")
                        .left(new BinaryExpression()
                                .type("BinaryExpression")
                                .left(new StringCondition()
                                        .type("StringCondition")
                                        .field("INDICATION")
                                        .value("C10BA02")
                                )
                                .operator(BinaryOperator.OR)
                                .right(new StringCondition()
                                        .type("StringCondition")
                                        .field("INDICATION")
                                        .value("C10BA05")
                                )
                        )
                        .operator(BinaryOperator.AND)
                        .right(new NumberCondition()
                                .type("NumberCondition")
                                .field("AGE")
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                .value(13)
                        )
                )
        );

        var dsls = List.of(
                "Klausul CLAUSE: (INDICATION = C10BA03) eller (INDICATION i C10BA02, C10BA05) og (AGE >= 13)",
                "Klausul CLAUSE: (INDICATION = C10BA03) eller ((INDICATION i C10BA02, C10BA05) og (AGE >= 13))",
                "Klausul CLAUSE: ((INDICATION = C10BA03) eller ((INDICATION i C10BA02, C10BA05) og (AGE >= 13)))",
                "Klausul CLAUSE: INDICATION = C10BA03 eller (INDICATION i C10BA02, C10BA05 og AGE >= 13)",
                "Klausul CLAUSE: INDICATION = C10BA03 eller INDICATION i C10BA02, C10BA05 og AGE >= 13"
        );


        dsls.forEach(dsl -> {
            System.out.println("Subject: " + dsl);
            Assertions.assertEquals(expected, mapper.map(dsl), "Unexpected mapping of: " + dsl);
        });
    }
}