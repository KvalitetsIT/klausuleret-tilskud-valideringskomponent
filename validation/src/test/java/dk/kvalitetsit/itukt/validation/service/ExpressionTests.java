package dk.kvalitetsit.itukt.validation.service;

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
    final static String inputIndication = "indication";
    final ValidationInput validationInput = new ValidationInput("", "", empty(), List.of(), inputAge, 0, inputIndication, empty());

    final static String
            inputAtcCode = "atcCode",
            inputFormCode = "formCode",
            inputRouteOfAdministrationCode = "routeOfAdministrationCode";
    final List<ExistingDrugMedication> existingMedications =
            List.of(new ExistingDrugMedication(inputAtcCode, inputFormCode, inputRouteOfAdministrationCode));
    final ValidationInput validationInputWithHistory =
            new ValidationInput("", "", empty(), List.of(), inputAge, 0, inputIndication, Optional.of(existingMedications));

    static void assertErrorMessage(String v, Optional<Expression.ValidationError> o) {
        assertEquals(Optional.of(v), o.map(Expression.ValidationError::errorMessage));
    }

    static Stream<Arguments> allValidNumberConditionCombinations() {
        return Stream.of(Operator.values())
                .flatMap(ExpressionTests::toValidAgeConditionExpression)
                .map(Arguments::of);
    }

    static Stream<AgeConditionExpression> toValidAgeConditionExpression(Operator op) {
        var validatingAges = switch (op) {
            case EQUAL -> Stream.of(inputAge);
            case LESS_THAN_OR_EQUAL_TO -> Stream.of(inputAge, inputAge + 1);
            case GREATER_THAN_OR_EQUAL_TO -> Stream.of(inputAge, inputAge - 1);
            case GREATER_THAN -> Stream.of(inputAge - 1);
            case LESS_THAN -> Stream.of(inputAge + 1);
        };
        return validatingAges.map(v -> new AgeConditionExpression(op, v));
    }

    static Stream<Arguments> allErrorNumberConditionCombinations() {
        return Stream.of(Operator.values())
                .map(ExpressionTests::toExpressionAndExpectedError)
                .map(Arguments::of);
    }

    static ExpressionAndExpectedError toExpressionAndExpectedError(Operator op) {
        var failingAgesWithError = switch (op) {
            case EQUAL -> new ExpectedAgeAndError(inputAge + 1, "alder skal være " + (inputAge + 1));
            case GREATER_THAN -> new ExpectedAgeAndError(inputAge + 1, "alder skal være større end " + (inputAge + 1));
            case LESS_THAN_OR_EQUAL_TO -> new ExpectedAgeAndError(inputAge - 1, "alder skal være mindre end eller lig " + (inputAge - 1));
            case GREATER_THAN_OR_EQUAL_TO -> new ExpectedAgeAndError(inputAge + 1, "alder skal være større end eller lig " + (inputAge + 1));
            case LESS_THAN -> new ExpectedAgeAndError(inputAge - 1, "alder skal være mindre end " + (inputAge - 1));
        };
        return new ExpressionAndExpectedError(
                new AgeConditionExpression(op, failingAgesWithError.age),
                failingAgesWithError.error);
    }

    record ExpectedAgeAndError(Integer age, String error) {}

    record ExpressionAndExpectedError(Expression expression, String expectedError) {}

    @ParameterizedTest
    @MethodSource("allValidNumberConditionCombinations")
    void validateNumber_noError(AgeConditionExpression expression) {
        assertEquals(empty(), expression.validates(validationInput));
    }


    @ParameterizedTest
    @MethodSource("allErrorNumberConditionCombinations")
    void validateNumber_error(ExpressionAndExpectedError expressionAndError) {
        assertErrorMessage(expressionAndError.expectedError, expressionAndError.expression.validates(validationInput));
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
        assertErrorMessage("(alder skal være 39 eller alder skal være 38) eller (alder skal være 37 eller alder skal være 36)", result);
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
        assertErrorMessage("alder skal være 39 eller indikation skal være indication-no-match", result);
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
        assertErrorMessage("alder skal være 39 eller alder skal være 39 og indikation skal være indication-no-match", result);
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
        assertErrorMessage("alder skal være 38 eller alder skal være 39 og (indikation skal være indication-no-match eller indikation skal være indication-no-match2)", result);
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
        assertErrorMessage("alder skal være 38 eller (indikation skal være indication-no-match eller indikation skal være indication-no-match2)", result);
    }

    private static Stream<Arguments> allAndCombinations() {
        return Stream.of(
                Arguments.of(inputAge, inputIndication, empty()),
                Arguments.of(inputAge - 1, inputIndication, Optional.of("alder skal være " + (inputAge - 1))),
                Arguments.of(inputAge, inputIndication + "-no-match", Optional.of("indikation skal være " + inputIndication + "-no-match")),
                Arguments.of(inputAge - 1, inputIndication + "-no-match", Optional.of("alder skal være " + (inputAge - 1) + " og " + "indikation skal være " + inputIndication + "-no-match"))
        );
    }

    @ParameterizedTest
    @MethodSource("allAndCombinations")
    void validate_andErrorShouldBeReturned(int age, String indication, Optional<String> expectedError) {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, age);
        var exp2 = new IndicationConditionExpression(indication);
        var combined = new BinaryExpression(exp1, BinaryExpression.Operator.AND, exp2);
        var result = combined.validates(validationInput);
        assertEquals(expectedError, result.map(Expression.ValidationError::errorMessage));
    }

    @Test
    void validate_existingMedication_ShouldSucceed() {
        var exp = new ExistingDrugMedicationConditionExpression(inputAtcCode, inputFormCode, inputRouteOfAdministrationCode);
        assertEquals(empty(), exp.validates(validationInputWithHistory));
    }

    private static Stream<Arguments> nonValidatingHistoryExpressionCodes() {
        return Stream.of(
                Arguments.of(inputAtcCode + "-no-match", inputFormCode, inputRouteOfAdministrationCode),
                Arguments.of(inputAtcCode, inputFormCode + "-no-match", inputRouteOfAdministrationCode),
                Arguments.of(inputAtcCode, inputFormCode, inputRouteOfAdministrationCode + "-no-match")
        );
    }

    @ParameterizedTest
    @MethodSource("nonValidatingHistoryExpressionCodes")
    void validate_existingMedication_errorShouldBeReturned(String atcCode, String formCode, String routeOfAdministrationCode) {
        var exp = new ExistingDrugMedicationConditionExpression(atcCode, formCode, routeOfAdministrationCode);
        Optional<Expression.ValidationError> result = exp.validates(validationInputWithHistory);
        assertErrorMessage("Tidligere medicinsk behandling med følgende påkrævet: ATC = " + atcCode + ", Formkode = " + formCode + ", Administrationsrutekode = " + routeOfAdministrationCode, result);
    }

    @ParameterizedTest
    @MethodSource("nonValidatingHistoryExpressionCodes")
    void validate_existingMedication_nestedErrorShouldBeReturned(String atcCode, String formCode, String routeOfAdministrationCode) {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, 18);
        var exp2 = new ExistingDrugMedicationConditionExpression(atcCode, formCode, routeOfAdministrationCode);
        var exp3 = new IndicationConditionExpression(inputIndication + "-no-match");
        var orExp = new BinaryExpression(exp2, BinaryExpression.Operator.OR, exp3);
        var andExp = new BinaryExpression(exp1, BinaryExpression.Operator.AND, orExp);

        Optional<Expression.ValidationError> result = andExp.validates(validationInputWithHistory);
        assertErrorMessage("alder skal være 18 og (Tidligere medicinsk behandling med følgende påkrævet: ATC = " +
                atcCode + ", Formkode = " +
                formCode + ", Administrationsrutekode = " +
                routeOfAdministrationCode +
                " eller indikation skal være indication-no-match)", result);
    }

    @ParameterizedTest
    @MethodSource("nonValidatingHistoryExpressionCodes")
    void validate_existingMedication_nestedErrorShouldBeReturned2(String atcCode, String formCode, String routeOfAdministrationCode) {
        var exp1 = new AgeConditionExpression(Operator.EQUAL, 18);
        var exp2 = new ExistingDrugMedicationConditionExpression(atcCode, formCode, routeOfAdministrationCode);
        var exp3 = new IndicationConditionExpression(inputIndication);
        var orExp = new BinaryExpression(exp2, BinaryExpression.Operator.AND, exp3);
        var andExp = new BinaryExpression(exp1, BinaryExpression.Operator.OR, orExp);

        Optional<Expression.ValidationError> result = andExp.validates(validationInputWithHistory);
        assertErrorMessage("alder skal være 18 eller Tidligere medicinsk behandling med følgende påkrævet: ATC = " +
                atcCode + ", Formkode = " +
                formCode + ", Administrationsrutekode = " +
                routeOfAdministrationCode, result);
    }
}