package dk.kvalitetsit.itukt.common.exceptions;

public class ServiceException extends RuntimeException {

    private static final String DEFAULT_SERVICE_EXCEPTION = "Noget gik galt";

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException() {
        super(DEFAULT_SERVICE_EXCEPTION);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
