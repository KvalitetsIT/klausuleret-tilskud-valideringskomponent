package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;

public record AgeConditionExpression(Operator operator, int value) implements Expression.Condition {
    public Optional<ValidationError> validates(ValidationInput validationInput) {
        var intValue = validationInput.citizenAge();
        var success = switch (operator) {
            case EQUAL -> intValue == this.value;
            case GREATER_THAN_OR_EQUAL_TO -> intValue >= this.value;
            case LESS_THAN_OR_EQUAL_TO -> intValue <= this.value;
            case GREATER_THAN -> intValue > this.value;
            case LESS_THAN -> intValue < this.value;
        };
        return success
                ? Optional.empty()
                : Optional.of(new ValidationError("alder " + getErrorOperator() + " " + value));
    }

    private String getErrorOperator() {
        return switch (operator) {
            case EQUAL -> "skal være";
            case GREATER_THAN_OR_EQUAL_TO -> "skal være større end eller lig";
            case LESS_THAN_OR_EQUAL_TO -> "skal være mindre end eller lig";
            case GREATER_THAN -> "skal være større end";
            case LESS_THAN -> "skal være mindre end";
        };
    }
}
