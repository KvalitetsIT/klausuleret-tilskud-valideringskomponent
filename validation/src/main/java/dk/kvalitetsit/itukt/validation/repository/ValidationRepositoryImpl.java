package dk.kvalitetsit.itukt.validation.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;

import java.time.Instant;

public class ValidationRepositoryImpl implements ValidationRepository {

    // Suggestion / draft
    public void insertValidationAttempt(String reportedBy, String createdBy, Instant timestamp,  boolean result) {
        throw new NotImplementedException();
    }

}
