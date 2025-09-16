package dk.kvalitetsit.itukt.common.filters;

import dk.kvalitetsit.itukt.common.exceptions.GenericApiException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;

class GenericExceptionHandlerTest {

    @Test
    void testGenericExceptionHandlerHandlesRuntimeExceptions() throws UnsupportedEncodingException {
        var filter = new GenericExceptionHandler();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/whatever");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            throw new RuntimeException();
        };

        GenericApiException thrown = Assertions.assertThrows(GenericApiException.class, () -> filter.doFilter(request, response, chain));
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus(), "In case a arbitrary exception is thrown this is expected to be mapped into a internal service error");
        Assertions.assertEquals("{\"message\": \"Der skete en ukendt fejl\"}", response.getContentAsString(), "Expected a default error message");
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertEquals(new GenericApiException().getMessage(), thrown.getMessage(), String.format("Expected the message of the %s", GenericApiException.class));
    }

    @Test
    void testGenericExceptionHandlerThrowsServiceExceptions() {
        var filter = new GenericExceptionHandler();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/whatever");
        MockHttpServletResponse response = new MockHttpServletResponse();

        var se = new GenericApiException();

        FilterChain chain = (req, res) -> {
            throw se;
        };
        GenericApiException thrown = Assertions.assertThrows(GenericApiException.class, () -> filter.doFilter(request, response, chain));

        Assertions.assertEquals(new GenericApiException().getMessage(), thrown.getMessage());
    }

}