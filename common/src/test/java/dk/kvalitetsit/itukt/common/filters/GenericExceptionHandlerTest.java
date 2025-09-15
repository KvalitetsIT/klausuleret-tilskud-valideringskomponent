package dk.kvalitetsit.itukt.common.filters;

import dk.kvalitetsit.itukt.common.configuration.CommonBeanRegistration;
import dk.kvalitetsit.itukt.common.exceptions.GenericApiException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static dk.kvalitetsit.itukt.common.Mock.CONFIG;

class GenericExceptionHandlerTest {

    @Test
    void testGenericExceptionHandlerHandlesRuntimeExceptions() {
        var registration = new CommonBeanRegistration(CONFIG);
        var filter = registration.genericExceptionHandler();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/whatever");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            throw new RuntimeException();
        };

        GenericApiException thrown = Assertions.assertThrows(GenericApiException.class, () -> filter.doFilter(request, response, chain));
        Assertions.assertEquals(new GenericApiException().getMessage(), thrown.getMessage(), "Expected the message of the default ServiceException");
    }

    @Test
    void testGenericExceptionHandlerThrowsServiceExceptions() {
        var registration = new CommonBeanRegistration(CONFIG);
        var filter = registration.genericExceptionHandler();

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