package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;


import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class ExpressionDtoDslMapperTest {

    @InjectMocks
    private ExpressionDtoDslMapper mapper;

    @Test
    void givenMultipleIndications_whenMap_thenMergeIntoList() {
        Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new IndicationCondition()
                        .type(ExpressionType.INDICATION)
                        .value("C10BA03")
                )
                .operator(BinaryOperator.OR)
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
                        ).operator(BinaryOperator.AND)
                        .right(new AgeCondition()
                                .type(ExpressionType.AGE)
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO)
                                .value(13)
                        )
                );

        String expected = "INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og ALDER >= 13";
        String actual = this.mapper.map(subject);
        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenAMixOfConditions_whenMap_thenMapCorrectly() {
        Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .left(new IndicationCondition().type(ExpressionType.INDICATION).value("C10BA03"))
                        .operator(BinaryOperator.OR)
                        .right(new BinaryExpression()
                                .type(ExpressionType.BINARY)
                                .left(new BinaryExpression()
                                        .type(ExpressionType.BINARY)
                                        .left(new IndicationCondition()
                                                .type(ExpressionType.INDICATION)
                                                .value("C10BA02"))
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
                        )
                ).operator(BinaryOperator.AND)
                .right(new ExistingDrugMedicationCondition()
                        .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                        .atcCode("ATC1")
                        .formCode("FORM1")
                        .routeOfAdministrationCode("ROUTE1")
                );

        String expected = "(INDIKATION = C10BA03 eller INDIKATION i [C10BA02, C10BA05] og ALDER >= 13) og EKSISTERENDE_LÆGEMIDDEL = {ATC = ATC1, FORM = FORM1, ROUTE = ROUTE1}";
        String actual = this.mapper.map(subject);
        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void givenAnExistingDrugMedicationConditionExpression_whenMap_thenMapCorrectly() {
        ExistingDrugMedicationCondition subject = new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("atcCode").formCode("formCode").routeOfAdministrationCode("routeOfAdministration");

        var expected = "EKSISTERENDE_LÆGEMIDDEL = {ATC = atcCode, FORM = formCode, ROUTE = routeOfAdministration}";
        var actual = this.mapper.map(subject);

        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithTwoExistingDrugConditions_whenMap_thenParseDrugCorrectly() {
        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C10B").formCode("TABLET").routeOfAdministrationCode("ORAL"))
                .operator(BinaryOperator.OR)
                .right(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("B01AC").formCode("INJEKTION").routeOfAdministrationCode("INTRAVENØS"));

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithMultipleExistingDrugConditions_whenMap_thenParseDrugCorrectly() {
        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C10B").formCode("TABLET").routeOfAdministrationCode("RECTAL"))
                .operator(BinaryOperator.OR)
                .right(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .left(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C20B").formCode("TABLET").routeOfAdministrationCode("ORAL"))
                        .operator(BinaryOperator.OR)
                        .right(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("B01AC").formCode("INJEKTION").routeOfAdministrationCode("INTRAVENØS"))
                );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = RECTAL}, {ATC = C20B, FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleExistingDrugConditionsAndOperators_whenMap_thenParseDrugCorrectly() {
        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new ExistingDrugMedicationCondition()
                        .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                        .atcCode("C10B").formCode("TABLET")
                        .routeOfAdministrationCode("RECTAL")
                )
                .operator(BinaryOperator.AND)
                .right(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .left(new ExistingDrugMedicationCondition().
                                type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                .atcCode("C20B").formCode("TABLET")
                                .routeOfAdministrationCode("ORAL")
                        )
                        .operator(BinaryOperator.OR)
                        .right(new ExistingDrugMedicationCondition()
                                .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                .atcCode("B01AC").formCode("INJEKTION")
                                .routeOfAdministrationCode("INTRAVENØS")
                        )
                );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = RECTAL} og EKSISTERENDE_LÆGEMIDDEL i [{ATC = C20B, FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithTwoExistingDrugConditionsThatCouldBeMerge_whenMap_thenMergeCorrectly() {
        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new BinaryExpression().type(ExpressionType.BINARY)
                        .left(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C10B").formCode("TABLET").routeOfAdministrationCode("ORAL"))
                        .operator(BinaryOperator.OR)
                        .right(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C20B").formCode("INJEKTION").routeOfAdministrationCode("INTRAVENØS"))
                )
                .operator(BinaryOperator.AND)
                .right(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .left(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C30B").formCode("TABLET").routeOfAdministrationCode("ORAL"))
                        .operator(BinaryOperator.OR)
                        .right(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C40B").formCode("INJEKTION").routeOfAdministrationCode("INTRAVENØS"))
                );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = ORAL}, {ATC = C20B, FORM = INJEKTION, ROUTE = INTRAVENØS}] og EKSISTERENDE_LÆGEMIDDEL i [{ATC = C30B, FORM = TABLET, ROUTE = ORAL}, {ATC = C40B, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleDrugConditionsThatCouldBeMerge_whenMap_thenMergeCorrectly() {
        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(
                        new BinaryExpression().type(ExpressionType.BINARY)
                                .left(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C10B").formCode("TABLET").routeOfAdministrationCode("ORAL"))
                                .operator(BinaryOperator.OR)
                                .right(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C20B").formCode("INJEKTION").routeOfAdministrationCode("INTRAVENØS")
                                )
                )
                .operator(BinaryOperator.OR)
                .right(
                        new BinaryExpression().type(ExpressionType.BINARY)
                                .left(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C30B").formCode("TABLET").routeOfAdministrationCode("ORAL"))
                                .operator(BinaryOperator.OR)
                                .right(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C40B").formCode("INJEKTION").routeOfAdministrationCode("INTRAVENØS")
                                )
                );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = ORAL}, {ATC = C20B, FORM = INJEKTION, ROUTE = INTRAVENØS}, {ATC = C30B, FORM = TABLET, ROUTE = ORAL}, {ATC = C40B, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithMultipleDrugConditionsThatCouldBeMerged_whenMap_thenMergeAndSeperateByAndCorrectly() {
        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .left(new ExistingDrugMedicationCondition()
                                .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                .atcCode("C00B")
                                .formCode("TABLET")
                                .routeOfAdministrationCode("ORAL")
                        )
                        .operator(BinaryOperator.AND)
                        .right(new BinaryExpression()
                                .type(ExpressionType.BINARY)
                                .left(new ExistingDrugMedicationCondition()
                                        .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                        .atcCode("C10B")
                                        .formCode("TABLET")
                                        .routeOfAdministrationCode("ORAL")
                                )
                                .operator(BinaryOperator.OR)
                                .right(new ExistingDrugMedicationCondition()
                                        .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                        .atcCode("C20B")
                                        .formCode("INJEKTION")
                                        .routeOfAdministrationCode("INTRAVENØS")
                                )
                        )
                )
                .operator(BinaryOperator.AND)
                .right(new BinaryExpression()
                        .left(new ExistingDrugMedicationCondition()
                                .type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C30B").formCode("TABLET").routeOfAdministrationCode("ORAL"))
                        .operator(BinaryOperator.OR)
                        .right(new BinaryExpression()
                                .left(new ExistingDrugMedicationCondition()
                                        .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                        .atcCode("C40B").formCode("TABLET")
                                        .routeOfAdministrationCode("ORAL"))
                                .operator(BinaryOperator.OR)
                                .right(new ExistingDrugMedicationCondition()
                                        .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                        .atcCode("C50B").formCode("INJEKTION")
                                        .routeOfAdministrationCode("INTRAVENØS"))
                        )
                );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL = " +
                "{ATC = C00B, FORM = TABLET, ROUTE = ORAL} og EKSISTERENDE_LÆGEMIDDEL i [" +
                "{ATC = C10B, FORM = TABLET, ROUTE = ORAL}, " +
                "{ATC = C20B, FORM = INJEKTION, ROUTE = INTRAVENØS}] og " +
                "EKSISTERENDE_LÆGEMIDDEL i [" +
                "{ATC = C30B, FORM = TABLET, ROUTE = ORAL}, " +
                "{ATC = C40B, FORM = TABLET, ROUTE = ORAL}, " +
                "{ATC = C50B, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithMultipleExistingDrugConditionsAndOperators_whenMap_thenParseDrugCorrectly2() {
        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C10B").formCode("TABLET").routeOfAdministrationCode("RECTAL"))
                .operator(BinaryOperator.OR)
                .right(new BinaryExpression()
                        .type(ExpressionType.BINARY)
                        .left(new ExistingDrugMedicationCondition()
                                .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                .atcCode("C20B")
                                .formCode("TABLET")
                                .routeOfAdministrationCode("ORAL")
                        )
                        .operator(BinaryOperator.AND)
                        .right(new ExistingDrugMedicationCondition()
                                .type(ExpressionType.EXISTING_DRUG_MEDICATION)
                                .atcCode("B01AC")
                                .formCode("INJEKTION")
                                .routeOfAdministrationCode("INTRAVENØS")
                        )
                );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = RECTAL} eller EKSISTERENDE_LÆGEMIDDEL = {ATC = C20B, FORM = TABLET, ROUTE = ORAL} og EKSISTERENDE_LÆGEMIDDEL = {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithExistingDrugConditionIncludingWildcards_whenMap_thenParseDrugCorrectly() {
        final Expression subject = new ExistingDrugMedicationCondition().type(ExpressionType.EXISTING_DRUG_MEDICATION).atcCode("C10B").formCode("*").routeOfAdministrationCode("*");

        String expected = "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = *}";
        String actual = mapper.map(subject);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithTwoAgeConditions_whenMap_thenMergeCorrectly() {
        final Expression subject = new BinaryExpression().type(ExpressionType.BINARY).left(
                new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(10)
        ).operator(BinaryOperator.OR).right(new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(20));

        String expected = "ALDER i [10, 20]";
        String actual = mapper.map(subject);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithTwoAgeConditions_whenMap_thenDoNotMerge() {
        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(10))
                .operator(BinaryOperator.AND)
                .right(new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(20));

        String expected = "ALDER = 10 og ALDER = 20";
        String actual = mapper.map(subject);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void map_givenABinaryExpressionWithAnOrOperatorThenCorrectlyPlaceParenthesis() {

        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new BinaryExpression().type(ExpressionType.BINARY)
                        .left(new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(10))
                        .operator(BinaryOperator.OR)
                        .right(new IndicationCondition().type(ExpressionType.INDICATION).value("blaa"))
                )
                .operator(BinaryOperator.AND)
                .right(new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(20));


        String actual = mapper.map(subject);

        String expected = "(ALDER = 10 eller INDIKATION = blaa) og ALDER = 20";

        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void map_givenABinaryExpressionWithAnAndOperatorThenCorrectlyPlaceParenthesis() {

        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new BinaryExpression().type(ExpressionType.BINARY)
                        .left(new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(10))
                        .operator(BinaryOperator.AND)
                        .right(new IndicationCondition().type(ExpressionType.INDICATION).value("blaa"))
                )
                .operator(BinaryOperator.AND)
                .right(new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(20));


        String actual = mapper.map(subject);

        String expected = "ALDER = 10 og INDIKATION = blaa og ALDER = 20";

        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void map_givenTwoBinaryOperatorThenCorrectlyPlaceParenthesis() {

        final Expression subject = new BinaryExpression()
                .type(ExpressionType.BINARY)
                .left(new BinaryExpression().type(ExpressionType.BINARY)
                        .left(new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(10))
                        .operator(BinaryOperator.AND)
                        .right(new IndicationCondition().type(ExpressionType.INDICATION).value("blaa"))
                )
                .operator(BinaryOperator.OR)
                .right(new AgeCondition().type(ExpressionType.AGE).operator(Operator.EQUAL).value(20));


        String actual = mapper.map(subject);

        String expected = "ALDER = 10 og INDIKATION = blaa eller ALDER = 20";

        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

}