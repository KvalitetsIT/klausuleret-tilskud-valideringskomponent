package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.service.CommonClauseService;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.validation.repository.entity.SkippedValidationEntity;

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
        var clauseIds = clauseService.getClauseIdsByErrorCodes(skippedErrorCodes);
        var skippedValidations = clauseIds.stream()
                .map(clauseId -> new SkippedValidationEntity(clauseId, actorId, personId))
                .toList();
        skippedValidationRepository.create(skippedValidations);
    }

    @Override
    public boolean shouldSkipValidation(String actorId, String personId, long clauseId) {
        return skippedValidationRepository.exists(new SkippedValidationEntity(clauseId, actorId, personId));
    }
}
