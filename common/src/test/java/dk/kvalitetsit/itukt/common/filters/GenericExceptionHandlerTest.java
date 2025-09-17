package dk.kvalitetsit.itukt.common.filters;

import dk.kvalitetsit.itukt.common.exceptions.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

class GenericExceptionHandlerTest {

    @Test
    void testGenericExceptionHandlerHandlesRuntimeExceptions() throws IOException, ServletException {
        var filter = new GenericExceptionHandler();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/whatever");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            throw new RuntimeException();
        };
        filter.doFilter(request, response, chain);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus(), "In case a arbitrary exception is thrown this is expected to be mapped into a internal service error");
        Assertions.assertEquals("{\"message\": \"Der skete en ukendt fejl\"}", response.getContentAsString(), "Expected a default error message");
        Assertions.assertEquals("application/json", response.getContentType());
    }

    @Test
    void testGenericExceptionHandlerThrowsServiceExceptions() {
        var filter = new GenericExceptionHandler();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/whatever");
        MockHttpServletResponse response = new MockHttpServletResponse();

        var se = Mockito.mock(ApiException.class);

        Mockito.when(se.getMessage()).thenReturn("blah");

        FilterChain chain = (req, res) -> {
            throw se;
        };

        ApiException thrown = Assertions.assertThrows(ApiException.class, () -> filter.doFilter(request, response, chain));

        Assertions.assertEquals("blah", thrown.getMessage());
    }

}