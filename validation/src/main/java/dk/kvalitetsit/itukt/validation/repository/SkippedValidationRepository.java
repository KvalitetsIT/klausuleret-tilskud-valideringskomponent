package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.validation.repository.entity.SkippedValidationEntity;

import java.util.List;

public interface SkippedValidationRepository {
    void create(List<SkippedValidationEntity> skippedValidations);
    boolean exists(SkippedValidationEntity skippedValidation);
}
