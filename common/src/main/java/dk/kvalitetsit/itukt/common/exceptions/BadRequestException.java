package dk.kvalitetsit.itukt.common.exceptions;

import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractApiException {
    public BadRequestException(DetailedError.DetailedErrorCodeEnum errorCode, String errorText) {
        super(HttpStatus.BAD_REQUEST, errorCode, errorText);
    }
}
