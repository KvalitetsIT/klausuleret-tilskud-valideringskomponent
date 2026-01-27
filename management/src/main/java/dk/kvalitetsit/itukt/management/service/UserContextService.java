package dk.kvalitetsit.itukt.management.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class UserContextService {
    private final Optional<String> userID;

    public UserContextService(HttpServletRequest request) {
        userID = Optional.ofNullable(request.getHeader("User-ID"));
    }

    public Optional<String> getUserID() {
        return userID;
    }
}
