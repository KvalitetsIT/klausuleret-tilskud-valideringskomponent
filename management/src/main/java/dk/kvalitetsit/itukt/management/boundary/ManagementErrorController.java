package dk.kvalitetsit.itukt.management.boundary;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.openapitools.model.DetailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class ManagementErrorController {
    private static final String INVALID_DSL = "Ugyldig klausulbetingelse. ";
    private final Logger logger = LoggerFactory.getLogger(ManagementErrorController.class);


    @ExceptionHandler(DslParserException.class)
    public ResponseEntity<DetailedError> handleDslParserException(DslParserException dslParserException, HttpServletRequest request) {
        logger.debug("Handling DslParserException", dslParserException);
        return switch (dslParserException) {
            case IncompleteDslException ignored ->
                    createBadRequestResponse(request, "Ufærdig klausulbetingelse");
            case UnexpectedValueException e ->
                    createBadRequestResponse(request, INVALID_DSL + "Uventet værdi: " + e.getValue());
            case UnexpectedAgeValueException e ->
                    createBadRequestResponse(request, INVALID_DSL + "Uventet værdi for alder: " + e.getValue());
            case UnexpectedEmptyMultiValueConditionException ignored ->
                    createBadRequestResponse(request, INVALID_DSL + "Betingelse med flere værdier må ikke være tom");
            case UnexpectedExistingDrugMedicationKeysException e ->
                    createBadRequestResponse(request, INVALID_DSL + "Eksisterende lægemiddel kan kun have værdier for: " +
                            String.join(", ", e.getValidKeys()));
        };
    }

    private static ResponseEntity<DetailedError> createBadRequestResponse(HttpServletRequest request, String message) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        var error = new DetailedError()
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .detailedErrorCode(DetailedError.DetailedErrorCodeEnum._1)
                .detailedError(message);

        return ResponseEntity.status(status.value()).body(error);
    }
}
