package dk.kvalitetsit.itukt.validation.service;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.validation.repository.entity.SkippedValidationEntity;

import java.util.List;

public class SkippedValidationServiceImpl implements SkippedValidationService {
    private final SkippedValidationRepository skippedValidationRepository;
    private final ClauseCache clauseCache;

    public SkippedValidationServiceImpl(SkippedValidationRepository skippedValidationRepository, ClauseCache clauseCache) {
        this.skippedValidationRepository = skippedValidationRepository;
        this.clauseCache = clauseCache;
    }

    @Override
    public void createSkippedValidations(String actorId, String personId, List<Integer> skippedErrorCodes) {
        List<Clause> clauses = skippedErrorCodes.stream()
                .flatMap(errorCode -> clauseCache.getByErrorCode(errorCode).stream())
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
