package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.List;

public class SkippedValidationServiceImpl implements SkippedValidationService{
    @Override
    public void createSkippedValidations(String actorId, String personId, List<Integer> skippedErrorCodes) {

    }

    @Override
    public boolean shouldSkipValidation(String actorId, String personId, Clause clause) {
        return false;
    }
}
