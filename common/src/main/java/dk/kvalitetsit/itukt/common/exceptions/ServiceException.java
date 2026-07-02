package dk.kvalitetsit.itukt.common.exceptions;

public abstract class ServiceException extends Exception {
    protected ServiceException(String message) {
        super(message);
    }

    protected ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
