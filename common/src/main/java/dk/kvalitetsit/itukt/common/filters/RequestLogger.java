package dk.kvalitetsit.itukt.common.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * A simple request filter which is to log any incoming before passing the request
 */
public class RequestLogger extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(RequestLogger.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String msg = String.format("Request { id: %s, Method: %s, Uri: %s, Response: %d, Time: %d ms }\n",
                    request.getRequestId(),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            );

            logger.debug(msg);
        }
    }
}