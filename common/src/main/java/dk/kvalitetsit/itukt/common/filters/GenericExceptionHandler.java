package dk.kvalitetsit.itukt.common.filters;

import dk.kvalitetsit.itukt.common.exceptions.AbstractApiException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class GenericExceptionHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (AbstractApiException | ServiceException a) {
            throw a;
        } catch (Throwable t) {
            logger.error(t.getMessage(), t); // <- logger den oprindelige fejl, mens nedenstående "default" ServiceException kastes i stedet
            throw new ServiceException();
        }
    }
}