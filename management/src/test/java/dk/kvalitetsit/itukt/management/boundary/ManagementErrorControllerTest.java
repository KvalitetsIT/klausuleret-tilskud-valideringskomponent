package dk.kvalitetsit.itukt.management.boundary;

import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.exceptions.*;
import org.junit.jupiter.api.Test;
import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagementErrorControllerTest {

    private final ManagementErrorController managementErrorController = new ManagementErrorController();

    @Test
    void handleDslParserException_WhenIncompleteDslException_ReturnsBadRequestWithExpectedMessage() {
        var response = managementErrorController.handleDslParserException(new IncompleteDslException(), request());

        assertBadRequest(response, "Ufærdig klausulbetingelse");
    }

    @Test
    void handleDslParserException_WhenUnexpectedValueException_ReturnsBadRequestWithExpectedMessage() {
        var response = managementErrorController.handleDslParserException(new UnexpectedValueException("abc"), request());

        assertBadRequest(response, "Ugyldig klausulbetingelse. Uventet værdi: abc");
    }

    @Test
    void handleDslParserException_WhenUnexpectedAgeValueException_ReturnsBadRequestWithExpectedMessage() {
        var response = managementErrorController.handleDslParserException(new UnexpectedAgeValueException("old"), request());

        assertBadRequest(response, "Ugyldig klausulbetingelse. Uventet værdi for alder: old");
    }

    @Test
    void handleDslParserException_WhenUnexpectedEmptyMultiValueConditionException_ReturnsBadRequestWithExpectedMessage() {
        var response = managementErrorController.handleDslParserException(new UnexpectedEmptyMultiValueConditionException(), request());

        assertBadRequest(response, "Ugyldig klausulbetingelse. Betingelse med flere værdier må ikke være tom");
    }

    @Test
    void handleDslParserException_WhenUnexpectedExistingDrugMedicationKeysException_ReturnsBadRequestWithExpectedMessage() {
        var response = managementErrorController.handleDslParserException(
                new UnexpectedExistingDrugMedicationKeysException(List.of("a", "b")),
                request()
        );

        assertBadRequest(response, "Ugyldig klausulbetingelse. Eksisterende lægemiddel kan kun have værdier for: a, b");
    }

    private static MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");
        return request;
    }

    private static void assertBadRequest(ResponseEntity<DetailedError> response, String expectedDetailedError) {
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("/test", response.getBody().getPath());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), response.getBody().getError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._1, response.getBody().getDetailedErrorCode());
        assertEquals(expectedDetailedError, response.getBody().getDetailedError());
    }
}