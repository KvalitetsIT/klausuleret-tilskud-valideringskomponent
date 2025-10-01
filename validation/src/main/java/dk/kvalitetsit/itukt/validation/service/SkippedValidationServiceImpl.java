package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.service.CommonClauseService;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepository;

import java.util.List;

public class SkippedValidationServiceImpl implements SkippedValidationService {
    private final SkippedValidationRepository skippedValidationRepository;
    private final CommonClauseService clauseService;

    public SkippedValidationServiceImpl(SkippedValidationRepository skippedValidationRepository, CommonClauseService clauseService) {
        this.skippedValidationRepository = skippedValidationRepository;
        this.clauseService = clauseService;
    }

    @Override
    public void createSkippedValidations(String actorId, String personId, List<Integer> skippedErrorCodes) {
    }

    @Override
    public boolean shouldSkipValidation(String actorId, String personId, Clause clause) {
        return false;
    }
}
