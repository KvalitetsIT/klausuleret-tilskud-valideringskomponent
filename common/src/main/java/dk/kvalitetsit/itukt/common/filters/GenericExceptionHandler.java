package dk.kvalitetsit.itukt.common.filters;

import dk.kvalitetsit.itukt.common.exceptions.ApiException;
import dk.kvalitetsit.itukt.common.exceptions.GenericApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class GenericExceptionHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ApiException a) {
            throw a;
        } catch (Throwable t) {
            logger.error(t.getMessage(), t); // <- logger den oprindelige fejl, mens nedenstående "default" ServiceException kastes i stedet
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Der skete en ukendt fejl\"}");
            throw new GenericApiException();
        }
    }
}