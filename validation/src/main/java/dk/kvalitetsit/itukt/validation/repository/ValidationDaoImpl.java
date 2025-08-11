package dk.kvalitetsit.itukt.validation.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class ValidationDaoImpl implements ValidationDao {

    // Suggestion / draft
    public void insertValidationAttempt(String reportedBy, String createdBy, Instant timestamp,  boolean result) {
        throw new NotImplementedException();
    }

}
