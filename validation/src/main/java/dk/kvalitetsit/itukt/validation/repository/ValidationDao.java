package dk.kvalitetsit.itukt.validation.repository;

import java.time.Instant;

public interface ValidationDao {
    void insertValidationAttempt(String reportedBy, String createdBy, Instant timestamp, boolean result);
}
