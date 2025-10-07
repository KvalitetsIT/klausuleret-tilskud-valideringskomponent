package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.validation.repository.entity.SkippedValidationEntity;

import java.util.List;

public class SkippedValidationServiceImpl implements SkippedValidationService {
    private final SkippedValidationRepository skippedValidationRepository;
    private final ClauseService clauseService;

    public SkippedValidationServiceImpl(SkippedValidationRepository skippedValidationRepository, ClauseService clauseService) {
        this.skippedValidationRepository = skippedValidationRepository;
        this.clauseService = clauseService;
    }

    @Override
    public void createSkippedValidations(String actorId, String personId, List<Integer> skippedErrorCodes) {
        List<Clause> clauses = skippedErrorCodes.stream()
                .flatMap(errorCode -> clauseService.getByErrorCode(errorCode).stream())
                .toList();
        var skippedValidations = clauses.stream()
                .map(clause -> new SkippedValidationEntity(clause.id(), actorId, personId))
                .toList();
        skippedValidationRepository.create(skippedValidations);
    }

    @Override
    public boolean shouldSkipValidation(String actorId, String personId, long clauseId) {
        return skippedValidationRepository.exists(new SkippedValidationEntity(clauseId, actorId, personId));
    }
}
