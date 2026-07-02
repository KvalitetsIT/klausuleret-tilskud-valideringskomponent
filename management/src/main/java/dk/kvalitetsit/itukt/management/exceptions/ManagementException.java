package dk.kvalitetsit.itukt.management.exceptions;

public sealed abstract class ManagementException extends Exception permits DslParserException {
    protected ManagementException(String message) {
        super(message);
    }

    protected ManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
