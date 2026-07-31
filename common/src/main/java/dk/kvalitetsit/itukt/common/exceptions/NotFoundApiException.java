package dk.kvalitetsit.itukt.common.exceptions;

import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;

public class NotFoundApiException extends ApiException {
    public NotFoundApiException(String message) {
        super(HttpStatus.NOT_FOUND, DetailedError.DetailedErrorCodeEnum._2, message);
    }
}
