package dk.kvalitetsit.itukt.common.repository;

import dk.kvalitetsit.itukt.common.repository.entity.SkippedValidationEntity;

import java.util.List;

public interface SkippedValidationRepository {
    void create(List<SkippedValidationEntity> skippedValidations);

    boolean exists(SkippedValidationEntity skippedValidation);

    void copySkippedValidation(long currentClauseId, long newClauseId);

}
