package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.management.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserContextServiceTest {

    @Test
    void getUserID_WithNoUserIdHeader_ThrowsException() {
        var userContextService = new UserContextService(new MockHttpServletRequest());
        assertThrows(InvalidInputException.class, userContextService::getUserID);
    }

    @Test
    void getUserID_WithUserIdHeader_ReturnsUserID() throws InvalidInputException {
        String expectedUserID = "test-user-id";
        var request = new MockHttpServletRequest();
        request.addHeader("User-ID", expectedUserID);
        var userContextService = new UserContextService(request);

        String userID = userContextService.getUserID();

        assertEquals(expectedUserID, userID);
    }
}