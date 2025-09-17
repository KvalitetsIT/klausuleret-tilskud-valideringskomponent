package dk.kvalitetsit.itukt.common.exceptions;

import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, DetailedError.DetailedErrorCodeEnum._20, message);
    }
}
