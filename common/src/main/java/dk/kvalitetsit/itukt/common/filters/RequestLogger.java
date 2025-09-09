package dk.kvalitetsit.itukt.common.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class RequestLogger extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            System.out.printf(
                    "Request { id: %s, Method: %s, Uri: %s, Response: %d, Time: %d ms }\n",
                    request.getRequestId(),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            );

            logger.debug(String.format("Request { id: %s, Method: %s, Uri: %s, Response: %d, Time: %d ms }\n",
                    request.getRequestId(),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            ));
        }
    }
}