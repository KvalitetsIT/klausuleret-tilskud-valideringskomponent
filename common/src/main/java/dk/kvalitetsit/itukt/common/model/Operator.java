package dk.kvalitetsit.itukt.common.model;

/**
 * Represents comparison operators mainly used in conditional expressions: {@link dk.kvalitetsit.itukt.common.model.Expression.Condition}.
 */
public enum Operator {
    EQUAL("="),
    GREATER_THAN_OR_EQUAL_TO(">="),
    LESS_THAN_OR_EQUAL_TO("<="),
    GREATER_THAN(">"),
    LESS_THAN("<");

    private final String value;
    Operator(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    /**
     * Returns the {@link Operator} corresponding to the given string value.
     *
     * @param value the string representation of the operator
     * @return the matching {@code Operator}
     * @throws IllegalArgumentException if the value does not match any operator
     */
    public static Operator fromValue(String value) {
        for (Operator op : Operator.values()) {
            if (op.getValue().equals(value)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}
