package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.common.exceptions.BadRequestApiException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class UserContextService {
    private final Optional<String> userID;

    public UserContextService(HttpServletRequest request) {
        userID = Optional.ofNullable(request.getHeader("User-ID"));
    }

    public String getUserID() {
        return userID.orElseThrow(() -> new BadRequestApiException("User-ID header is missing"));
    }
}
