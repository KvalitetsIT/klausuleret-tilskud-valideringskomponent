package dk.kvalitetsit.itukt.management.exceptions;

public sealed abstract class ManagementException extends Exception permits
        DslParserException,
        NotFoundException,
        InvalidInputException,
        RequiresForceException {

    protected ManagementException(String message) {
        super(message);
    }
}
