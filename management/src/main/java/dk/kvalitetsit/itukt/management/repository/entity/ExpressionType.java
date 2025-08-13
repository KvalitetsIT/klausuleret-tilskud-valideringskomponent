package dk.kvalitetsit.itukt.management.repository.entity;

public enum ExpressionType {

    CONDITION("condition_expression"),
    BINARY("binary_expression"),
    PARENTHESIZED("parenthesized_expression");

    private final String value;

    ExpressionType(String value) {
        this.value = value;
    }

    public static ExpressionType fromValue(String value) {
        for (ExpressionType op : ExpressionType.values()) {
            if (op.getValue().equals(value)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public String getValue() {
        return value;
    }
}
