package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.common.exceptions.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class UserContextService {
    private final Optional<String> userID;

    public UserContextService(HttpServletRequest request) {
        userID = Optional.ofNullable(request.getHeader("User-ID"));
    }

    public String getUserID() {
        return userID.orElseThrow(() -> new BadRequestException("User-ID header is missing"));
    }
}
