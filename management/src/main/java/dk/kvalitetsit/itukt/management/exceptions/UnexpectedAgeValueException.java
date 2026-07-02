package dk.kvalitetsit.itukt.management.exceptions;

public final class UnexpectedAgeValueException extends DslParserException {
    private final String value;

    public UnexpectedAgeValueException(String value) {
        super("Unexpected age value: " + value);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
