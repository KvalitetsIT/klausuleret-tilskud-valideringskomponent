package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.List;

public interface SkippedValidationService {
    void createSkippedValidations(String actorId, String personId, List<Integer> skippedErrorCodes);
    boolean shouldSkipValidation(String actorId, String personId, Clause clause);
}
