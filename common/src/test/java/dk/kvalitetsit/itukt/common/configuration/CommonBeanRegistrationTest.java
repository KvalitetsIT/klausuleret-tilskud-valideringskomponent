package dk.kvalitetsit.itukt.common.configuration;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommonBeanRegistrationTest {


    private static CommonConfiguration CONFIG = new CommonConfiguration(
            List.of(""),
            new DatasourceConfiguration("", "", "",
                    new ConnectionConfiguration("", null, null)
            )
    );

    @Test
    void oncePerRequestFilter_shouldLogRequestDetails() throws ServletException, IOException {
        var registration = new CommonBeanRegistration(CONFIG);

        var filter = registration.oncePerRequestFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/whatever");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // Capture System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            filter.doFilter(request, response, chain);
        } finally {
            System.setOut(originalOut);
        }

        // Assert
        String logOutput = outContent.toString();
        assertThat(logOutput)
                .contains("Request { id:")   // UUID
                .contains("Method: GET")
                .contains("Uri: /whatever")
                .contains("Response: 200")
                .contains("Time:");
    }

    @Test
    void testGenericExceptionHandlerHandlesRuntimeExceptions() {
        var registration = new CommonBeanRegistration(CONFIG);
        var filter = registration.genericExceptionHandler();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/whatever");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            throw new RuntimeException();
        };

        ServiceException thrown = Assertions.assertThrows(ServiceException.class, () -> filter.doFilter(request, response, chain));
        Assertions.assertEquals(new ServiceException().getMessage(), thrown.getMessage(), "Expected the message of the default ServiceException");

    }

    @Test
    void testGenericExceptionHandlerThrowsServiceExceptions() {
        var registration = new CommonBeanRegistration(CONFIG);
        var filter = registration.genericExceptionHandler();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/whatever");
        MockHttpServletResponse response = new MockHttpServletResponse();

        var se = new ServiceException("TEST MESSAGE");

        FilterChain chain = (req, res) -> {
            throw se;
        };
        ServiceException thrown = Assertions.assertThrows(ServiceException.class, () -> filter.doFilter(request, response, chain));

        Assertions.assertEquals("TEST MESSAGE", thrown.getMessage());
    }


}