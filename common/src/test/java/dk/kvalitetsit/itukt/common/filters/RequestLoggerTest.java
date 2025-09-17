package dk.kvalitetsit.itukt.common.filters;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RequestLoggerTest {

    @Test
    void oncePerRequestFilter_shouldLogRequestDetails() throws ServletException, IOException {

        Logger mockLogger = mock(Logger.class);

        try (MockedStatic<LoggerFactory> mockedFactory = Mockito.mockStatic(LoggerFactory.class)) {
            // Change this line to handle any logger request
            mockedFactory.when(() -> LoggerFactory.getLogger(Mockito.any(Class.class))).thenReturn(mockLogger);
            mockedFactory.when(() -> LoggerFactory.getLogger(Mockito.any(String.class))).thenReturn(mockLogger);

            var filter = new RequestLogger();

            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/whatever");
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();

            filter.doFilter(request, response, chain);

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            // We still verify the call from our logger, which we expect to be at the 'info' level
            verify(mockLogger).debug(captor.capture());

            String logOutput = captor.getValue();
            assertThat(logOutput)
                    .contains("Request { id:")
                    .contains("Method: GET")
                    .contains("Uri: /whatever")
                    .contains("Response: 200")
                    .contains("Time:");
        }
    }

}