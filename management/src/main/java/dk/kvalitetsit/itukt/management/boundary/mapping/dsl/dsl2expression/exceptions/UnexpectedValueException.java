package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions;

public final class UnexpectedValueException extends DslParserException {
    private final String value;

    public UnexpectedValueException(String value) {
        super("Unexpected value: " + value);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
