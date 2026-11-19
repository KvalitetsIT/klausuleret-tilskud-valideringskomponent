package dk.kvalitetsit.itukt.common.exceptions;

import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;

public class ExpressionValidationApiException extends ApiException {
    public ExpressionValidationApiException(String message) {
        super(HttpStatus.BAD_REQUEST, DetailedError.DetailedErrorCodeEnum.VALIDATION, message);
    }
}
