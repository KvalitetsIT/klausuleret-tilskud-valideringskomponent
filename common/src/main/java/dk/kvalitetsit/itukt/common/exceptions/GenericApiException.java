package dk.kvalitetsit.itukt.common.exceptions;

import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;

public class GenericApiException extends ApiException {
    public GenericApiException() {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                DetailedError.DetailedErrorCodeEnum._20,
                "Der skete en ukendt fejl"
        );
    }
}
