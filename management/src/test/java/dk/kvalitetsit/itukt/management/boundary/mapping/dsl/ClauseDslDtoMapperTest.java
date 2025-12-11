package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.clause.ClauseDslDtoMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;
import org.openapitools.model.Error;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ClauseDslDtoMapperTest {


    @InjectMocks
    private ClauseDslDtoMapper mapper;

    @Test
    void givenTwoValidDslWithAndWithoutParenthesis_whenMap_thenAssertAndHasHigherPrecedence() {

        final var expected = new ClauseInput("CLAUSE", new BinaryExpression()
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
                ),
                "blaah"
        );

        List<DslInput> validDSLs = Stream.of(
                        "Klausul CLAUSE: (INDIKATION = C10BA03) eller (INDIKATION i [C10BA02, C10BA05]) og (ALDER >= 13)",
                        "Klausul CLAUSE: INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og ALDER >= 13",
                        "Klausul CLAUSE: INDIKATION = C10BA03 eller (INDIKATION i [C10BA02, C10BA05] og ALDER >= 13)",
                        "Klausul CLAUSE: (((INDIKATION = C10BA03) eller (((INDIKATION i [C10BA02, C10BA05] og ALDER >= 13)))))"
                )
                .map(x -> new DslInput("blaah", x))
                .toList();

        validDSLs.forEach(dsl -> Assertions.assertEquals(mapper.map(dsl), expected, "Unexpected mapping of: " + dsl + " - The AND operator is expected to have higher precedence"));
    }


    @Test
    void givenDeeplyNestedDsl_whenMap_thenCorrectExpressionTree() {
        final ClauseInput expected = new ClauseInput("DEEP",
                new BinaryExpression(
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
                ), "blaah"
        );

        List<DslInput> subjects = Stream.of(
                        "Klausul DEEP: ((INDIKATION = X eller INDIKATION = Y) og (INDIKATION = Z eller INDIKATION = W)) og (ALDER >= 10)",
                        "Klausul DEEP: ((INDIKATION = X eller INDIKATION = Y) og (INDIKATION = Z eller INDIKATION = W)) og ALDER >= 10"
                )
                .map(x -> new DslInput("blaah", x))
                .toList();


        subjects.forEach(subject -> Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject));

    }

    @Test
    void givenDeeplyNestedDsl_whenMap_thenCorrectExpressionTree_2() {
        final ClauseInput expected = new ClauseInput("DEEP",
                new BinaryExpression(new BinaryExpression(
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
                ),
                "blaah"
        );
        DslInput subject = new DslInput("blaah", "Klausul DEEP: (INDIKATION = X eller INDIKATION = Y) og ((INDIKATION = Z eller INDIKATION = W) og ALDER >= 10)");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithLists_whenMap_thenExpandListCorrectly() {
        final ClauseInput expected = new ClauseInput(
                "LIST",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA01", ExpressionType.INDICATION),
                                BinaryOperator.OR,
                                new IndicationCondition("C10BA02", ExpressionType.INDICATION),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.OR,
                        new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                        ExpressionType.BINARY
                ),
                "blaah"
        );
        List<DslInput> validDSLs = Stream.of(
                "Klausul LIST: INDIKATION i [C10BA01, C10BA02, C10BA03]",
                "Klausul LIST: (INDIKATION i [C10BA01, C10BA02]) eller INDIKATION = C10BA03",
                "Klausul LIST: ((INDIKATION i [C10BA01, C10BA02]) eller INDIKATION = C10BA03)"
        ).map(x -> new DslInput("blaah", x)).toList();
        validDSLs.forEach(dsl -> Assertions.assertEquals(expected, mapper.map(dsl), "Unexpected mapping of: " + dsl));
    }

    @Test
    void givenDslWithNumericConditions_whenMap_thenOperatorsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE", new BinaryExpression(
                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 18, ExpressionType.AGE),
                BinaryOperator.AND,
                new AgeCondition(Operator.LESS_THAN_OR_EQUAL_TO, 65, ExpressionType.AGE),
                ExpressionType.BINARY
        ), "blaah");
        DslInput subject = new DslInput("blaah", "Klausul CLAUSE: ALDER >= 18 og ALDER <= 65");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenABinaryExpressionWithTwoNumericConditions_whenMap_thenOperatorsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput(
                "CLAUSE",
                new BinaryExpression(
                        new AgeCondition(Operator.GREATER_THAN, 0, ExpressionType.AGE),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.LESS_THAN, 100, ExpressionType.AGE),
                        ExpressionType.BINARY
                ),
                "blaah"
        );
        DslInput subject = new DslInput("blaah", "Klausul CLAUSE: ALDER > 0 eller ALDER < 100");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithNumericCondition_whenMap_thenEqualIsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE", new AgeCondition(Operator.EQUAL, 42, ExpressionType.AGE),
                "blaah");
        DslInput subject = new DslInput("blaah", "Klausul CLAUSE: ALDER = 42");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithSpecialityCondition_whenMap_thenItIsParsedCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE", new DoctorSpecialityCondition("LÆGE", ExpressionType.DOCTOR_SPECIALITY),
                "blaah");
        DslInput subject = new DslInput("blaah", "Klausul CLAUSE: LÆGESPECIALE = læge");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithOrSpecialityCondition_whenMap_thenItIsParsedCorrectly() {
        var expression = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new DoctorSpecialityCondition("LÆGE1", ExpressionType.DOCTOR_SPECIALITY))
                .operator(BinaryOperator.OR)
                .right(new DoctorSpecialityCondition("LÆGE2", ExpressionType.DOCTOR_SPECIALITY));
        final ClauseInput expected = new ClauseInput("CLAUSE", expression,
                "blaah");
        DslInput subject = new DslInput("blaah", "Klausul CLAUSE: LÆGESPECIALE i [læge1, læge2]");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleExistingDrugConditions_whenMap_thenParseDrugCorrectly() {
        final ClauseInput expected = new ClauseInput("DRUG", new BinaryExpression(
                new ExistingDrugMedicationCondition("C10B", "TABLET", "ORAL", ExpressionType.EXISTING_DRUG_MEDICATION),
                BinaryOperator.OR,
                new ExistingDrugMedicationCondition("B01AC", "INJEKTION", "INTRAVENØS", ExpressionType.EXISTING_DRUG_MEDICATION),
                ExpressionType.BINARY
        ), "blaah"
        );

        DslInput subject = new DslInput("blaah", "Klausul DRUG: EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = tablet, ROUTE = oral}, {ATC = B01AC, FORM = injektion, ROUTE = intravenøs}]");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithExistingDrugConditionIncludingWildcards_whenMap_thenParseDrugCorrectly() {
        final ClauseInput expected = new ClauseInput("DRUG", new ExistingDrugMedicationCondition("C10B", "*", "*", ExpressionType.EXISTING_DRUG_MEDICATION),
                "blaah");
        DslInput subject = new DslInput("blaah", "Klausul DRUG: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = *}");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithoutParentheses_whenMap_thenAndHasHigherPrecedence() {
        final ClauseInput expected = new ClauseInput("PRECEDENCE",
                new BinaryExpression(
                        new IndicationCondition("X", ExpressionType.INDICATION),
                        BinaryOperator.OR,
                        new BinaryExpression(
                                new IndicationCondition("Y", ExpressionType.INDICATION),
                                BinaryOperator.AND,
                                new IndicationCondition("Z", ExpressionType.INDICATION),
                                ExpressionType.BINARY
                        ),
                        ExpressionType.BINARY),
                "blaah"
        );

        DslInput subject_1 = new DslInput("blaah", "Klausul PRECEDENCE: INDIKATION = X eller INDIKATION = Y og INDIKATION = Z");
        DslInput subject_2 = new DslInput("blaah", "Klausul PRECEDENCE: INDIKATION = X eller (INDIKATION = Y og INDIKATION= Z)");

        Assertions.assertEquals(expected, mapper.map(subject_1), "Unexpected mapping of: " + subject_1);
        Assertions.assertEquals(expected, mapper.map(subject_2), "Unexpected mapping of: " + subject_2);
    }

    @Test
    void givenDslWithParentheses_whenMap_thenParenthesesHaveHigherPrecedence() {
        final ClauseInput expected = new ClauseInput("PRECEDENCE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("X", ExpressionType.INDICATION),
                                BinaryOperator.OR,
                                new IndicationCondition("Y", ExpressionType.INDICATION),
                                ExpressionType.BINARY
                        ), BinaryOperator.AND,
                        new IndicationCondition("Z", ExpressionType.INDICATION),
                        ExpressionType.BINARY),
                "blaah"

        );

        final DslInput subject = new DslInput("blaah", "Klausul PRECEDENCE: (INDIKATION = X eller INDIKATION = Y) og INDIKATION = Z");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleBinaryOperationsAndAndWhitespace_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                                BinaryOperator.AND,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                        ExpressionType.BINARY),
                "blaah"
        );

        final DslInput subject = new DslInput("blaah", "Klausul CLAUSE: INDIKATION =          C10BA03    OG ALDER >= 13 eLLer ALDER = 10");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleBinaryOperationsAndAllLowercase_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                                BinaryOperator.AND,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                        ExpressionType.BINARY),
                "blaah"
        );

        final DslInput subject = new DslInput("blaah", "klausul clause: INDIKATION = c10ba03 og ALDER >= 13 eller ALDER = 10");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithMultipleBinaryOperationsAndRandomCasing_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                                BinaryOperator.AND,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                        ExpressionType.BINARY),
                "blaah"
        );

        final DslInput subject = new DslInput("blaah", "kLausul CLAUSE: indiKaTion = C10BA03 OG ALDER >= 13 ELLER aLder = 10)");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleOrOperations_whenMap_thenParseCorrectly() {
        final ClauseInput expected = new ClauseInput("CLAUSE",
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationCondition("C10BA03", ExpressionType.INDICATION),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 13, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                        ExpressionType.BINARY),
                "blaah"
        );

        final DslInput subject = new DslInput("blaah", "Klausul CLAUSE: INDIKATION = C10BA03 eller ALDER >= 13 eller ALDER = 10");
        Assertions.assertEquals(expected, mapper.map(subject), "Unexpected mapping of: " + subject);
    }

    @Test
    void givenSeveralValidDslWithParenthesesPositionedDifferently_whenMap_thenReturnSameClause() {

        var expected = new ClauseInput("CLAUSE", new BinaryExpression()
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
                ),
                "blaah"
        );

        var dsls = Stream.of(
                "Klausul CLAUSE: (INDIKATION = C10BA03) eller (INDIKATION i [C10BA02, C10BA05]) og (ALDER >= 13)",
                "Klausul CLAUSE: (INDIKATION = C10BA03) eller ((INDIKATION i [C10BA02, C10BA05]) og (ALDER >= 13))",
                "Klausul CLAUSE: ((INDIKATION = C10BA03) eller ((INDIKATION i [C10BA02, C10BA05]) og (ALDER >= 13)))",
                "Klausul CLAUSE: INDIKATION = C10BA03 eller (INDIKATION i [C10BA02, C10BA05] og ALDER >= 13)",
                "Klausul CLAUSE: INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og ALDER >= 13"
        ).map(x -> new DslInput("blaah", x)).toList();

        dsls.forEach(dsl -> Assertions.assertEquals(expected, mapper.map(dsl), "Unexpected mapping of: " + dsl));
    }


    @Test
    void givenAnExistingDrugMedicationDsL_whenMap_thenIgnoreOrderOfFields() {
        final ClauseInput expected = new ClauseInput("BLAAH",
                new BinaryExpression(
                        new ExistingDrugMedicationCondition("C10B", "TABLET", "ORAL", ExpressionType.EXISTING_DRUG_MEDICATION),
                        BinaryOperator.OR,
                        new ExistingDrugMedicationCondition("B01AC", "INJEKTION", "INTRAVENØS",
                                ExpressionType.EXISTING_DRUG_MEDICATION),
                        ExpressionType.BINARY
                ),
                "blaah"
        );

        var dsls = Stream.of(
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}]",
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL i [{FORM = TABLET, ATC = C10B, ROUTE = ORAL}, {ROUTE = INTRAVENØS, ATC = B01AC, FORM = INJEKTION}]",
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B,FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, ROUTE = INTRAVENØS, FORM = INJEKTION}]",
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, ROUTE = ORAL, FORM = TABLET}, {ATC = B01AC, ROUTE = INTRAVENØS, FORM = INJEKTION}]"
        ).map(x -> new DslInput("blaah", x)).toList();

        dsls.forEach(dsl -> Assertions.assertEquals(expected, mapper.map(dsl), "Unexpected mapping of: " + dsl));
    }


    @Test
    void givenAnExistingDrugMedicationDsLWithWildcardFields_whenMap_thenMapCorrectly() {
        var cases = Map.of(
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = ORAL}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "C10B",
                                "TABLET",
                                "ORAL",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"
                ),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = *}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "C10B",
                                "TABLET",
                                "*",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"
                ),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = ORAL}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "C10B",
                                "*",
                                "ORAL",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"
                ),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE = ORAL}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "*",
                                "TABLET",
                                "ORAL",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"
                ),

                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = *}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "C10B",
                                "*",
                                "*",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = *, ROUTE =  ORAL}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "*",
                                "*",
                                "ORAL",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = TABLET, ROUTE =  *}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "*",
                                "TABLET",
                                "*",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = *, FORM = *, ROUTE =  *}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "*",
                                "*",
                                "*",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah")
        );

        cases.forEach((key, value) -> {
            DslInput input = new DslInput("blaah", key);
            Assertions.assertEquals(value, mapper.map(input), "Unexpected mapping of: " + input.getDsl());
        });
    }



    @Test
    void givenAnExistingDrugMedicationDsLWithIgnoredFields_whenMap_thenMapCorrectly() {
        var cases = Map.of(
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = ORAL}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "C10B",
                                "TABLET",
                                "ORAL",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"
                ),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "C10B",
                                "TABLET",
                                "*",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"
                ),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, ROUTE = ORAL}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "C10B",
                                "*",
                                "ORAL",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"
                ),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {FORM = TABLET, ROUTE = ORAL}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "*",
                                "TABLET",
                                "ORAL",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"
                ),

                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "C10B",
                                "*",
                                "*",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {ROUTE =  ORAL}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "*",
                                "*",
                                "ORAL",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah"),
                "Klausul BLAAH: EKSISTERENDE_LÆGEMIDDEL = {FORM = TABLET}",
                new ClauseInput("BLAAH",
                        new ExistingDrugMedicationCondition(
                                "*",
                                "TABLET",
                                "*",
                                ExpressionType.EXISTING_DRUG_MEDICATION
                        ),
                        "blaah")
        );

        cases.forEach((key, value) -> {
            DslInput input = new DslInput("blaah", key);
            Assertions.assertEquals(value, mapper.map(input), "Unexpected mapping of: " + input.getDsl());
        });
    }



    @Test
    void givenADsLWithMultipleMergedAgeConditions_whenMap_thenCorrectlySetOperatorToEquals() {

        var expected = new ClauseInput("BLAAH",
                new BinaryExpression(
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.EQUAL, 20, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.EQUAL, 30, ExpressionType.AGE),
                        ExpressionType.BINARY
                ),
                "blaah"
        );

        var dsl = "Klausul BLAAH: ALDER i [10, 20, 30]";

        Assertions.assertEquals(expected, mapper.map(new DslInput("blaah", dsl)), "Unexpected mapping of: " + dsl);
    }


    @Test
    void givenADsLWithMultipleAgeConditionsWithVaryingOperators_whenMap_thenMapCorrectly() {

        var expected = new ClauseInput("BLAAH",
                new BinaryExpression(
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 20, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.OR,
                        new AgeCondition(Operator.LESS_THAN, 30, ExpressionType.AGE),
                        ExpressionType.BINARY
                ),
                "blaah"
        );

        var dsl = "Klausul BLAAH: ALDER = 10 ELLER ALDER >= 20 ELLER ALDER < 30";

        Assertions.assertEquals(expected, mapper.map(new DslInput("blaah", dsl)), "Unexpected mapping of: " + dsl);
    }


    @Test
    void givenADsLWithMultipleAgeConditionsWithVaryingOperators_whenMap_thenHandlePrecedenceCorrectly() {

        var expected = new ClauseInput("BLAAH",
                new BinaryExpression(
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.GREATER_THAN_OR_EQUAL_TO, 20, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.AND,
                        new AgeCondition(Operator.EQUAL, 30, ExpressionType.AGE),
                        ExpressionType.BINARY
                ),
                "blaah"
        );

        var dsl = "Klausul BLAAH: (ALDER = 10 ELLER ALDER >= 20) og ALDER = 30";

        Assertions.assertEquals(expected, mapper.map(new DslInput("blaah", dsl)), "Unexpected mapping of: " + dsl);

    }

    @Test
    void givenADsLWithMultipleAgeConditions_whenMap_thenHandlePrecedenceCorrectly() {

        var expected = new ClauseInput("BLAAH",
                new BinaryExpression(
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                                BinaryOperator.OR,
                                new AgeCondition(Operator.EQUAL, 20, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        BinaryOperator.AND,
                        new AgeCondition(Operator.EQUAL, 30, ExpressionType.AGE),
                        ExpressionType.BINARY
                ),
                "blaah"
        );

        var dsl = "Klausul BLAAH: (ALDER i [10, 20]) og ALDER = 30";

        Assertions.assertEquals(expected, mapper.map(new DslInput("blaah", dsl)), "Unexpected mapping of: " + dsl);

    }


    @Test
    void givenADsLWithMultipleNonMergedAgeConditions_whenMap_thenMapCorrectly() {

        var expected = new ClauseInput("BLAAH",
                new BinaryExpression(
                        new AgeCondition(Operator.EQUAL, 10, ExpressionType.AGE),
                        BinaryOperator.OR,
                        new BinaryExpression(
                                new AgeCondition(Operator.EQUAL, 20, ExpressionType.AGE),
                                BinaryOperator.AND,
                                new AgeCondition(Operator.EQUAL, 30, ExpressionType.AGE),
                                ExpressionType.BINARY
                        ),
                        ExpressionType.BINARY
                ),
                "blaah"
        );

        var dsl = "Klausul BLAAH: ALDER = 10 eller alder = 20 og ALDER = 30";

        Assertions.assertEquals(expected, mapper.map(new DslInput("blaah", dsl)), "Unexpected mapping of: " + dsl);

    }


    @Test
    void givenDSLWithInvalidUsageOfMultipleValues_whenMap_thenThrowError() {
        var dsl = "Klausul BLAAH: ALDER i 10, 20, 30";
        Assertions.assertThrows(
                RuntimeException.class,
                () -> mapper.map(new DslInput("blaah", dsl)),
                "If a dsl with an invalid array is given an exception is expected to be thrown");
    }










}