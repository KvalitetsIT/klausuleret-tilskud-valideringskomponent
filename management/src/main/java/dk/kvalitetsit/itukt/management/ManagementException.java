package dk.kvalitetsit.itukt.management;

public abstract class ManagementException extends Exception {
    protected ManagementException(String message) {
        super(message);
    }

    protected ManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
