package dk.kvalitetsit.itukt.common.model;

public record NumberConditionExpression(Field field, Operator operator, int value) implements Expression.Condition {
    @Override
    public boolean validates(ValidationInput validationInput) {
        var fieldValue = validationInput.getByField(field());
        return fieldValue instanceof Integer intValue &&
                switch (operator) {
                    case EQUAL -> intValue == this.value;
                    case GREATER_THAN_OR_EQUAL_TO -> intValue >= this.value;
                    case LESS_THAN_OR_EQUAL_TO -> intValue <= this.value;
                    case GREATER_THAN -> intValue > this.value;
                    case LESS_THAN -> intValue < this.value;
                };
    }
}
