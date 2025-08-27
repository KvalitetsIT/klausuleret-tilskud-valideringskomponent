package dk.kvalitetsit.itukt.common.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommonBeanRegistrationTest {


    @Test
    void oncePerRequestFilter_shouldLogRequestDetails() throws Exception {
        // Arrange
        var config = new CommonConfiguration(
                List.of(""),
                new DatasourceConfiguration("", "", "",
                        new ConnectionConfiguration("", null, null)
                )
        );

        var registration = new CommonBeanRegistration(config);

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
}