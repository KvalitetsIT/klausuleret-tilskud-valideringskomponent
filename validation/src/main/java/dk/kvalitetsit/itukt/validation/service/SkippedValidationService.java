package dk.kvalitetsit.itukt.validation.service;

import java.util.List;

public interface SkippedValidationService {
    void createSkippedValidations(String actorId, String personId, List<Integer> skippedErrorCodes);
    boolean shouldSkipValidation(String actorId, String personId, long clauseId);
}
