package dk.kvalitetsit.itukt.common.model;

public enum Operator {
    EQUAL("="),
    IN("i"),
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
    public static Operator fromValue(String value) {
        for (Operator op : Operator.values()) {
            if (op.getValue().equals(value)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}
