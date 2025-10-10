package dk.kvalitetsit.itukt.common.model.featureTests;

import dk.kvalitetsit.itukt.common.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTests {
    final static int inputAge = 40;
    final static String inputIndication = "input-indication";
    final ValidationInput validationInput = new ValidationInput("", "", empty(), List.of(), inputAge, 0, inputIndication, empty());

    final static String
            inputAtcCode = "atcCode",
            inputFormCode = "formCode",
            inputRouteOfAdministrationCode = "routeOfAdministrationCode";
    final List<ExistingDrugMedication> existingMedications =
            List.of(new ExistingDrugMedication(inputAtcCode, inputFormCode, inputRouteOfAdministrationCode));
    final ValidationInput validationInputWithHistory =
            new ValidationInput("", "", empty(), List.of(), inputAge, 0, inputIndication, Optional.of(existingMedications));

    static void assertErrorMessage(String v, Optional<ValidationFailed> o) {
        assertEquals(Optional.of(v), o.map(failed -> assertInstanceOf(dk.kvalitetsit.itukt.common.model.ValidationError.class, failed).toErrorString()));
    }

    @Test
    void validateNumber_equal_noError() {
        var expression = new AgeConditionExpression(Operator.EQUAL, inputAge);
        assertEquals(empty(), expression.validates(validationInput));
    }

    @Test
    void validateNumber_lessThanOrEqual_inputAge_noError() {
        var expression = new AgeConditionExpression(Operator.LESS_THAN_OR_EQUAL_TO, inputAge);
        assertEquals(empty(), expression.validates(validationInput));
    }

    @Test
    void validateNumber_lessThanOrEqual_inputAgePlusOne_noError() {
        var expression = new AgeConditionExpression(Operator.LESS_THAN_OR_EQUAL_TO, inputAge + 1);
        assertEquals(empty(), expression.validates(validationInput));
    }

    @Test
    void validateNumber_greaterThanOrEqual_inputAge_noError() {
        var expression = new AgeConditionExpression(Operator.GREATER_THAN_OR_EQUAL_TO, inputAge);
        assertEquals(empty(), expression.validates(validationInput));
    }

    @Test
    void validateNumber_greaterThanOrEqual_inputAgeMinusOne_noError() {
        var expression = new AgeConditionExpression(Operator.GREATER_THAN_OR_EQUAL_TO, inputAge - 1);
        assertEquals(empty(), expression.validates(validationInput));
    }

    @Test
    void validateNumber_greaterThan_inputAgeMinusOne_noError() {
        var expression = new AgeConditionExpression(Operator.GREATER_THAN, inputAge - 1);
        assertEquals(empty(), expression.validates(validationInput));
    }

    @Test
    void validateNumber_lessThan_inputAgePlusOne_noError() {
        var expression = new AgeConditionExpression(Operator.LESS_THAN, inputAge + 1);
        assertEquals(empty(), expression.validates(validationInput));
    }

    @Test
    void validateNumber_equal_error() {
        var expr = new AgeConditionExpression(Operator.EQUAL, inputAge + 1);
        assertErrorMessage("alder skal være " + (inputAge + 1), expr.validates(validationInput));
    }

    @Test
    void validateNumber_greaterThan_error() {
        var expr = new AgeConditionExpression(Operator.GREATER_THAN, inputAge + 1);
        assertErrorMessage("alder skal være større end " + (inputAge + 1), expr.validates(validationInput));
    }

    @Test
    void validateNumber_lessThanOrEqual_error() {
        var expr = new AgeConditionExpression(Operator.LESS_THAN_OR_EQUAL_TO, inputAge - 1);
        assertErrorMessage("alder skal være mindre end eller lig " + (inputAge - 1), expr.validates(validationInput));
    }

    @Test
    void validateNumber_greaterThanOrEqual_error() {
        var expr = new AgeConditionExpression(Operator.GREATER_THAN_OR_EQUAL_TO, inputAge + 1);
        assertErrorMessage("alder skal være større end eller lig " + (inputAge + 1), expr.validates(validationInput));
    }

    @Test
    void validateNumber_lessThan_error() {
        var expr = new AgeConditionExpression(Operator.LESS_THAN, inputAge - 1);
        assertErrorMessage("alder skal være mindre end " + (inputAge - 1), expr.validates(validationInput));
    }

    @Test
    void validateString_noError() {
        var validates = new IndicationConditionExpression(inputIndication).validates(validationInput);
        assertEquals(empty(), validates);
    }

    @Test
    void validateString_error() {
        var requiredValue = inputIndication + "-no-match";
        var validates = new IndicationConditionExpression(requiredValue).validates(validationInput);
        assertErrorMessage("indikation skal være " + requiredValue, validates);
    }

    @Test
    void validateHistory_whenNoHistoryIsInInputAndOrExpressionValidatesThenNoErrorShouldBeReturned() {
        var exp1 = new ExistingDrugMedicationConditionExpression("atc", "form", "routeOfAdmin");
        var exp2 = new AgeConditionExpression(Operator.EQUAL, inputAge);
        var or = new BinaryExpression(exp1, BinaryExpression.Operator.OR, exp2);
        var result = or.validates(validationInput);
        assertEquals(empty(), result);
    }

    @Test
    void validate_errorShouldBeReturned() {
        var expression = new AgeConditionExpression(Operator.EQUAL, inputAge + 1);
        var result = expression.validates(validationInput);
        assertErrorMessage("alder skal være 41", result);
    }

    @Test
    void validate_errorShouldBeReturned2() {
        var expression = new AgeConditionExpression(Operator.GREATER_THAN, inputAge);
        var result = expression.validates(validationInput);
        assertErrorMessage("alder skal være større end 40", result);
    }

    @Test
    void validate_errorShouldBeReturned3() {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, inputAge - 2);
        var exp2 = new AgeConditionExpression(Operator.GREATER_THAN, inputAge);
        var combined = new BinaryExpression(exp1, BinaryExpression.Operator.OR, exp2);
        var result = combined.validates(validationInput);
        assertErrorMessage("alder skal være 38 eller alder skal være større end 40", result);
    }

    @Test
    void validate_errorShouldBeReturned4() {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, inputAge - 1);
        var exp2 = new AgeConditionExpression(Operator.EQUAL, inputAge - 2);
        var combined1 = new BinaryExpression(exp1, BinaryExpression.Operator.OR, exp2);

        var exp3 = new AgeConditionExpression(Operator.EQUAL, inputAge - 3);
        var exp4 = new AgeConditionExpression(Operator.EQUAL, inputAge - 4);
        var combined2 = new BinaryExpression(exp3, BinaryExpression.Operator.OR, exp4);

        var combined = new BinaryExpression(combined1, BinaryExpression.Operator.OR, combined2);

        var result = combined.validates(validationInput);
        assertErrorMessage("alder skal være 39 eller alder skal være 38 eller alder skal være 37 eller alder skal være 36", result);
    }

    @Test
    void validate_errorShouldBeReturned5() {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, inputAge - 1);
        var exp2 = new IndicationConditionExpression(inputIndication);
        var combined1 = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);

        var exp3 = new AgeConditionExpression(Operator.EQUAL, inputAge);
        var exp4 = new IndicationConditionExpression(inputIndication + "-no-match");
        var combined2 = new BinaryExpression(exp3, BinaryExpression.Operator.AND, exp4);

        var combined = new BinaryExpression(combined1, BinaryExpression.Operator.OR, combined2);

        var result = combined.validates(validationInput);
        assertErrorMessage("alder skal være 39 eller indikation skal være input-indication-no-match", result);
    }

    @Test
    void validate_errorShouldBeReturned6() {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, inputAge - 1);
        var exp2 = new IndicationConditionExpression(inputIndication);
        var combined1 = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);

        var exp3 = new AgeConditionExpression(Operator.EQUAL, inputAge - 1);
        var exp4 = new IndicationConditionExpression(inputIndication + "-no-match");
        var combined2 = new BinaryExpression(exp3, BinaryExpression.Operator.AND, exp4);

        var combined = new BinaryExpression(combined1, BinaryExpression.Operator.OR, combined2);

        var result = combined.validates(validationInput);
        assertErrorMessage("alder skal være 39 eller alder skal være 39 og indikation skal være input-indication-no-match", result);
    }

    @Test
    void validate_errorShouldBeReturned7() {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, inputAge - 2);
        var exp2 = new IndicationConditionExpression(inputIndication);
        var combined1 = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);

        var exp3 = new AgeConditionExpression(Operator.EQUAL, inputAge - 1);
        var exp4 = new IndicationConditionExpression(inputIndication + "-no-match");
        var exp5 = new IndicationConditionExpression(inputIndication + "-no-match2");
        var combined2 = new BinaryExpression(exp4, BinaryExpression.Operator.OR, exp5);
        var combined3 = new BinaryExpression(exp3, BinaryExpression.Operator.AND, combined2);

        var combined = new BinaryExpression(combined1, BinaryExpression.Operator.OR, combined3);

        var result = combined.validates(validationInput);
        assertErrorMessage("alder skal være 38 eller alder skal være 39 og (indikation skal være input-indication-no-match eller indikation skal være input-indication-no-match2)", result);
    }

    @Test
    void validate_errorShouldBeReturned8() {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, inputAge - 2);
        var exp2 = new IndicationConditionExpression(inputIndication);
        var combined1 = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);

        var exp3 = new AgeConditionExpression(Operator.EQUAL, inputAge - 1);
        var exp4 = new IndicationConditionExpression(inputIndication);
        var exp5 = new IndicationConditionExpression(inputIndication + "-no-match2");
        var combined2 = new BinaryExpression(exp4, BinaryExpression.Operator.OR, exp5);
        var combined3 = new BinaryExpression(exp3, BinaryExpression.Operator.AND, combined2);

        var combined = new BinaryExpression(combined1, BinaryExpression.Operator.OR, combined3);

        var result = combined.validates(validationInput);
        assertErrorMessage("alder skal være 38 eller alder skal være 39", result);
    }

    @Test
    void validate_errorShouldBeReturned9() {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, inputAge - 2);
        var exp2 = new IndicationConditionExpression(inputIndication);
        var combined1 = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);

        var exp3 = new AgeConditionExpression(Operator.EQUAL, inputAge);
        var exp4 = new IndicationConditionExpression(inputIndication + "-no-match");
        var exp5 = new IndicationConditionExpression(inputIndication + "-no-match2");
        var combined2 = new BinaryExpression(exp4, BinaryExpression.Operator.OR, exp5);
        var combined3 = new BinaryExpression(exp3, BinaryExpression.Operator.AND, combined2);

        var combined = new BinaryExpression(combined1, BinaryExpression.Operator.OR, combined3);

        var result = combined.validates(validationInput);
        assertErrorMessage("alder skal være 38 eller indikation skal være input-indication-no-match eller indikation skal være input-indication-no-match2", result);
    }

    @Test
    void validate_and_noError() {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, inputAge);
        var exp2 = new IndicationConditionExpression(inputIndication);
        var combined = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);
        var result = combined.validates(validationInput);
        assertEquals(empty(), result);
    }

    @Test
    void validate_and_ageError() {
        var age = inputAge - 1;
        var exp1 = new AgeConditionExpression(Operator.EQUAL, age);
        var exp2 = new IndicationConditionExpression(inputIndication);
        var combined = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);
        var result = combined.validates(validationInput);
        assertErrorMessage("alder skal være " + age, result);
    }

    @Test
    void validate_and_indicationError() {
        var indication = inputIndication + "-no-match";
        var exp1 = new AgeConditionExpression(Operator.EQUAL, inputAge);
        var exp2 = new IndicationConditionExpression(indication);
        var combined = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);
        var result = combined.validates(validationInput);
        assertErrorMessage("indikation skal være " + indication, result);
    }

    @Test
    void validate_and_bothError() {
        var age = inputAge - 1;
        var indication = inputIndication + "-no-match";
        var exp1 = new AgeConditionExpression(Operator.EQUAL, age);
        var exp2 = new IndicationConditionExpression(indication);
        var combined = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);
        var result = combined.validates(validationInput);
        assertErrorMessage("alder skal være " + age + " og indikation skal være " + indication, result);
    }

    @Test
    void validate_existingMedication_ShouldSucceed() {
        var exp = new ExistingDrugMedicationConditionExpression(inputAtcCode, inputFormCode, inputRouteOfAdministrationCode);
        assertEquals(empty(), exp.validates(validationInputWithHistory));
    }

    @Test
    void validate_existingMedication_error_atcMismatch() {
        var atcCode = inputAtcCode + "-no-match";
        var formCode = inputFormCode;
        var route = inputRouteOfAdministrationCode;
        var exp = new ExistingDrugMedicationConditionExpression(atcCode, formCode, route);
        var result = exp.validates(validationInputWithHistory);

        assertErrorMessage(
                "Tidligere medicinsk behandling med følgende påkrævet: ATC = " + atcCode +
                        ", Formkode = " + formCode +
                        ", Administrationsrutekode = " + route,
                result
        );
    }

    @Test
    void validate_existingMedication_error_formMismatch() {
        var atcCode = inputAtcCode;
        var formCode = inputFormCode + "-no-match";
        var route = inputRouteOfAdministrationCode;
        var exp = new ExistingDrugMedicationConditionExpression(atcCode, formCode, route);
        var result = exp.validates(validationInputWithHistory);

        assertErrorMessage(
                "Tidligere medicinsk behandling med følgende påkrævet: ATC = " + atcCode +
                        ", Formkode = " + formCode +
                        ", Administrationsrutekode = " + route,
                result
        );
    }

    @Test
    void validate_existingMedication_error_routeMismatch() {
        var atcCode = inputAtcCode;
        var formCode = inputFormCode;
        var route = inputRouteOfAdministrationCode + "-no-match";
        var exp = new ExistingDrugMedicationConditionExpression(atcCode, formCode, route);
        var result = exp.validates(validationInputWithHistory);

        assertErrorMessage(
                "Tidligere medicinsk behandling med følgende påkrævet: ATC = " + atcCode +
                        ", Formkode = " + formCode +
                        ", Administrationsrutekode = " + route,
                result
        );
    }

    @Test
    void validate_existingMedication_nestedError_atcMismatch() {
        var atcCode = inputAtcCode + "-no-match";
        var formCode = inputFormCode;
        var route = inputRouteOfAdministrationCode;
        var exp1 = new AgeConditionExpression(Operator.EQUAL, 18);
        var exp2 = new ExistingDrugMedicationConditionExpression(atcCode, formCode, route);
        var exp3 = new IndicationConditionExpression(inputIndication + "-no-match");
        var orExp = new BinaryExpression(exp2, BinaryExpression.Operator.OR, exp3);
        var andExp = new BinaryExpression(exp1, BinaryExpression.Operator.AND, orExp);

        var result = andExp.validates(validationInputWithHistory);

        assertErrorMessage(
                "alder skal være 18 og (Tidligere medicinsk behandling med følgende påkrævet: ATC = " +
                        atcCode + ", Formkode = " + formCode + ", Administrationsrutekode = " + route +
                        " eller indikation skal være input-indication-no-match)",
                result
        );
    }

    @Test
    void validate_existingMedication_nestedError_formMismatch() {
        var atcCode = inputAtcCode;
        var formCode = inputFormCode + "-no-match";
        var route = inputRouteOfAdministrationCode;
        var exp1 = new AgeConditionExpression(Operator.EQUAL, 18);
        var exp2 = new ExistingDrugMedicationConditionExpression(atcCode, formCode, route);
        var exp3 = new IndicationConditionExpression(inputIndication + "-no-match");
        var orExp = new BinaryExpression(exp2, BinaryExpression.Operator.OR, exp3);
        var andExp = new BinaryExpression(exp1, BinaryExpression.Operator.AND, orExp);

        var result = andExp.validates(validationInputWithHistory);

        assertErrorMessage(
                "alder skal være 18 og (Tidligere medicinsk behandling med følgende påkrævet: ATC = " +
                        atcCode + ", Formkode = " + formCode + ", Administrationsrutekode = " + route +
                        " eller indikation skal være input-indication-no-match)",
                result
        );
    }

    @Test
    void validate_existingMedication_nestedError_routeMismatch() {
        var atcCode = inputAtcCode;
        var formCode = inputFormCode;
        var route = inputRouteOfAdministrationCode + "-no-match";
        var exp1 = new AgeConditionExpression(Operator.EQUAL, 18);
        var exp2 = new ExistingDrugMedicationConditionExpression(atcCode, formCode, route);
        var exp3 = new IndicationConditionExpression(inputIndication + "-no-match");
        var orExp = new BinaryExpression(exp2, BinaryExpression.Operator.OR, exp3);
        var andExp = new BinaryExpression(exp1, BinaryExpression.Operator.AND, orExp);

        var result = andExp.validates(validationInputWithHistory);

        assertErrorMessage(
                "alder skal være 18 og (Tidligere medicinsk behandling med følgende påkrævet: ATC = " +
                        atcCode + ", Formkode = " + formCode + ", Administrationsrutekode = " + route +
                        " eller indikation skal være input-indication-no-match)",
                result
        );
    }

    @Test
    void validate_existingMedication_nestedError2_atcMismatch() {
        var atcCode = inputAtcCode + "-no-match";
        var formCode = inputFormCode;
        var route = inputRouteOfAdministrationCode;
        var exp1 = new AgeConditionExpression(Operator.EQUAL, 18);
        var exp2 = new ExistingDrugMedicationConditionExpression(atcCode, formCode, route);
        var exp3 = new IndicationConditionExpression(inputIndication);
        var orExp = new BinaryExpression(exp2, BinaryExpression.Operator.AND, exp3);
        var andExp = new BinaryExpression(exp1, BinaryExpression.Operator.OR, orExp);

        var result = andExp.validates(validationInputWithHistory);

        assertErrorMessage(
                "alder skal være 18 eller Tidligere medicinsk behandling med følgende påkrævet: ATC = " +
                        atcCode + ", Formkode = " + formCode + ", Administrationsrutekode = " + route,
                result
        );
    }

    @Test
    void validate_existingMedication_nestedError2_formMismatch() {
        var atcCode = inputAtcCode;
        var formCode = inputFormCode + "-no-match";
        var route = inputRouteOfAdministrationCode;
        var exp1 = new AgeConditionExpression(Operator.EQUAL, 18);
        var exp2 = new ExistingDrugMedicationConditionExpression(atcCode, formCode, route);
        var exp3 = new IndicationConditionExpression(inputIndication);
        var orExp = new BinaryExpression(exp2, BinaryExpression.Operator.AND, exp3);
        var andExp = new BinaryExpression(exp1, BinaryExpression.Operator.OR, orExp);

        var result = andExp.validates(validationInputWithHistory);

        assertErrorMessage(
                "alder skal være 18 eller Tidligere medicinsk behandling med følgende påkrævet: ATC = " +
                        atcCode + ", Formkode = " + formCode + ", Administrationsrutekode = " + route,
                result
        );
    }

    @Test
    void validate_existingMedication_nestedError2_routeMismatch() {
        var atcCode = inputAtcCode;
        var formCode = inputFormCode;
        var route = inputRouteOfAdministrationCode + "-no-match";
        var exp1 = new AgeConditionExpression(Operator.EQUAL, 18);
        var exp2 = new ExistingDrugMedicationConditionExpression(atcCode, formCode, route);
        var exp3 = new IndicationConditionExpression(inputIndication);
        var orExp = new BinaryExpression(exp2, BinaryExpression.Operator.AND, exp3);
        var andExp = new BinaryExpression(exp1, BinaryExpression.Operator.OR, orExp);

        var result = andExp.validates(validationInputWithHistory);

        assertErrorMessage(
                "alder skal være 18 eller Tidligere medicinsk behandling med følgende påkrævet: ATC = " +
                        atcCode + ", Formkode = " + formCode + ", Administrationsrutekode = " + route,
                result
        );
    }
}