package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.common.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class ExpressionModelDslMapperTest {

    @InjectMocks
    private ExpressionModelDslMapper mapper;

    @Test
    void givenMultipleIndications_whenMap_thenMergeIntoList() {
        Expression subject = new BinaryExpression(
                new IndicationConditionExpression("C10BA03"),
                BinaryExpression.Operator.OR,
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationConditionExpression("C10BA02"),
                                BinaryExpression.Operator.OR,
                                new IndicationConditionExpression("C10BA05")
                        ),
                        BinaryExpression.Operator.AND,
                        new AgeConditionExpression(Operator.GREATER_THAN_OR_EQUAL_TO, 13)
                )
        );
        String expected = "INDICATION = C10BA03 eller INDICATION i [C10BA02, C10BA05] og AGE >= 13";
        String actual = this.mapper.map(subject);
        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenAMixOfConditions_whenMap_thenMapCorrectly() {
        Expression subject = new BinaryExpression(new BinaryExpression(
                new IndicationConditionExpression("C10BA03"),
                BinaryExpression.Operator.OR,
                new BinaryExpression(
                        new BinaryExpression(
                                new IndicationConditionExpression("C10BA02"),
                                BinaryExpression.Operator.OR,
                                new IndicationConditionExpression("C10BA05")
                        ),
                        BinaryExpression.Operator.AND,
                        new AgeConditionExpression(Operator.GREATER_THAN_OR_EQUAL_TO, 13)
                )
        ), BinaryExpression.Operator.AND, new ExistingDrugMedicationConditionExpression("ATC1", "FORM1", "ROUTE1")
        );
        String expected = "INDICATION = C10BA03 eller INDICATION i [C10BA02, C10BA05] og AGE >= 13 og EKSISTERENDE_LÆGEMIDDEL = {ATC = ATC1, FORM = FORM1, ROUTE = ROUTE1}";
        String actual = this.mapper.map(subject);
        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }



    @Test
    void givenAnExistingDrugMedicationConditionExpression_whenMap_thenMapCorrectly() {
        ExistingDrugMedicationConditionExpression subject = new ExistingDrugMedicationConditionExpression("atcCode", "formCode", "routeOfAdministration");

        var expected = "EKSISTERENDE_LÆGEMIDDEL = {ATC = atcCode, FORM = formCode, ROUTE = routeOfAdministration}";
        var actual = this.mapper.map(subject);

        assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithTwoExistingDrugConditions_whenMap_thenParseDrugCorrectly() {
        final Expression subject = new BinaryExpression(
                new ExistingDrugMedicationConditionExpression("C10B", "TABLET", "ORAL"),
                BinaryExpression.Operator.OR,
                new ExistingDrugMedicationConditionExpression("B01AC", "INJEKTION", "INTRAVENØS")
        );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithMultipleExistingDrugConditions_whenMap_thenParseDrugCorrectly() {
        final Expression subject = new BinaryExpression(
                new ExistingDrugMedicationConditionExpression("C10B", "TABLET", "RECTAL"),
                BinaryExpression.Operator.OR,
                new BinaryExpression(
                        new ExistingDrugMedicationConditionExpression("C20B", "TABLET", "ORAL"),
                        BinaryExpression.Operator.OR,
                        new ExistingDrugMedicationConditionExpression("B01AC", "INJEKTION", "INTRAVENØS")
                )
        );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = RECTAL}, {ATC = C20B, FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleExistingDrugConditionsAndOperators_whenMap_thenParseDrugCorrectly() {
        final Expression subject = new BinaryExpression(
                new ExistingDrugMedicationConditionExpression("C10B", "TABLET", "RECTAL"),
                BinaryExpression.Operator.AND,
                new BinaryExpression(
                        new ExistingDrugMedicationConditionExpression("C20B", "TABLET", "ORAL"),
                        BinaryExpression.Operator.OR,
                        new ExistingDrugMedicationConditionExpression("B01AC", "INJEKTION", "INTRAVENØS")
                )
        );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = RECTAL} og EKSISTERENDE_LÆGEMIDDEL i [{ATC = C20B, FORM = TABLET, ROUTE = ORAL}, {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithTwoExistingDrugConditionsThatCouldBeMerge_whenMap_thenMergeCorrectly() {
        final Expression subject = new BinaryExpression(
                new BinaryExpression(
                        new ExistingDrugMedicationConditionExpression("C10B", "TABLET", "ORAL"),
                        BinaryExpression.Operator.OR,
                        new ExistingDrugMedicationConditionExpression("C20B", "INJEKTION", "INTRAVENØS")
                ),
                BinaryExpression.Operator.AND,
                new BinaryExpression(
                        new ExistingDrugMedicationConditionExpression("C30B", "TABLET", "ORAL"),
                        BinaryExpression.Operator.OR,
                        new ExistingDrugMedicationConditionExpression("C40B", "INJEKTION", "INTRAVENØS")
                )
        );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = ORAL}, {ATC = C20B, FORM = INJEKTION, ROUTE = INTRAVENØS}] og EKSISTERENDE_LÆGEMIDDEL i [{ATC = C30B, FORM = TABLET, ROUTE = ORAL}, {ATC = C40B, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }

    @Test
    void givenDslWithMultipleDrugConditionsThatCouldBeMerge_whenMap_thenMergeCorrectly() {
        final Expression subject = new BinaryExpression(
                new BinaryExpression(
                        new ExistingDrugMedicationConditionExpression("C10B", "TABLET", "ORAL"),
                        BinaryExpression.Operator.OR,
                        new ExistingDrugMedicationConditionExpression("C20B", "INJEKTION", "INTRAVENØS")
                ),
                BinaryExpression.Operator.OR,
                new BinaryExpression(
                        new ExistingDrugMedicationConditionExpression("C30B", "TABLET", "ORAL"),
                        BinaryExpression.Operator.OR,
                        new ExistingDrugMedicationConditionExpression("C40B", "INJEKTION", "INTRAVENØS")
                )
        );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL i [{ATC = C10B, FORM = TABLET, ROUTE = ORAL}, {ATC = C20B, FORM = INJEKTION, ROUTE = INTRAVENØS}, {ATC = C30B, FORM = TABLET, ROUTE = ORAL}, {ATC = C40B, FORM = INJEKTION, ROUTE = INTRAVENØS}]";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithMultipleDrugConditionsThatCouldBeMerged_whenMap_thenMergeAndSeperateByAndCorrectly() {
        final Expression subject = new BinaryExpression(
                new BinaryExpression(
                        new ExistingDrugMedicationConditionExpression("C00B", "TABLET", "ORAL"),
                        BinaryExpression.Operator.AND,
                        new BinaryExpression(
                                new ExistingDrugMedicationConditionExpression("C10B", "TABLET", "ORAL"),
                                BinaryExpression.Operator.OR,
                                new ExistingDrugMedicationConditionExpression("C20B", "INJEKTION", "INTRAVENØS")
                        )
                ),
                BinaryExpression.Operator.AND,
                new BinaryExpression(
                        new ExistingDrugMedicationConditionExpression("C30B", "TABLET", "ORAL"),
                        BinaryExpression.Operator.OR,
                        new BinaryExpression(
                                new ExistingDrugMedicationConditionExpression("C40B", "TABLET", "ORAL"),
                                BinaryExpression.Operator.OR,
                                new ExistingDrugMedicationConditionExpression("C50B", "INJEKTION", "INTRAVENØS")
                        )
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
        final Expression subject = new BinaryExpression(
                new ExistingDrugMedicationConditionExpression("C10B", "TABLET", "RECTAL"),
                BinaryExpression.Operator.OR,
                new BinaryExpression(
                        new ExistingDrugMedicationConditionExpression("C20B", "TABLET", "ORAL"),
                        BinaryExpression.Operator.AND,
                        new ExistingDrugMedicationConditionExpression("B01AC", "INJEKTION", "INTRAVENØS")
                )
        );

        String actual = mapper.map(subject);
        String expected = "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = TABLET, ROUTE = RECTAL} eller EKSISTERENDE_LÆGEMIDDEL = {ATC = C20B, FORM = TABLET, ROUTE = ORAL} og EKSISTERENDE_LÆGEMIDDEL = {ATC = B01AC, FORM = INJEKTION, ROUTE = INTRAVENØS}";
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


    @Test
    void givenDslWithExistingDrugConditionIncludingWildcards_whenMap_thenParseDrugCorrectly() {
        final Expression subject = new ExistingDrugMedicationConditionExpression("C10B", "*", "*");

        String expected = "EKSISTERENDE_LÆGEMIDDEL = {ATC = C10B, FORM = *, ROUTE = *}";
        String actual = mapper.map(subject);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + subject);
    }


}