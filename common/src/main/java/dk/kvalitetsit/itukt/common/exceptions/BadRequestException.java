package dk.kvalitetsit.itukt.common.exceptions;

import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {
    public BadRequestException(String errorText) {
        super(HttpStatus.BAD_REQUEST, DetailedError.DetailedErrorCodeEnum._1, errorText);
    }
}
