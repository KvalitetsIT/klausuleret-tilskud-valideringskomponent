package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.common.exceptions.BadRequestApiException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserContextServiceTest {

    @Test
    void getUserID_WithNoUserIdHeader_ThrowsException() {
        var userContextService = new UserContextService(new MockHttpServletRequest());
        assertThrows(BadRequestApiException.class, userContextService::getUserID);
    }

    @Test
    void getUserID_WithUserIdHeader_ReturnsUserID() {
        String expectedUserID = "test-user-id";
        var request = new MockHttpServletRequest();
        request.addHeader("User-ID", expectedUserID);
        var userContextService = new UserContextService(request);

        String userID = userContextService.getUserID();

        assertEquals(expectedUserID, userID);
    }
}