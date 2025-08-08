package dk.kvalitetsit.itukt.validation.repository;

import java.time.Instant;

public interface ValidationRepository {
    void insertValidationAttempt(String reportedBy, String createdBy, Instant timestamp, boolean result);
}
