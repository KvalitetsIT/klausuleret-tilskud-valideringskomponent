package dk.kvalitetsit.itukt.common.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This exception handler maps unexpected exceptions to a 500 response
 * It is implemented as a filter, so it won't catch Spring exceptions, or other exceptions that are handled elsewhere.
 */
public class GenericExceptionHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Der skete en ukendt fejl\"}");
        }
    }
}