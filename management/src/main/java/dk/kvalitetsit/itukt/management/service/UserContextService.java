package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.management.exceptions.InvalidInputException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class UserContextService {
    private final Optional<String> userID;

    public UserContextService(HttpServletRequest request) {
        userID = Optional.ofNullable(request.getHeader("User-ID"));
    }

    public String getUserID() throws InvalidInputException {
        return userID.orElseThrow(() -> new InvalidInputException("User-ID header is missing"));
    }
}
