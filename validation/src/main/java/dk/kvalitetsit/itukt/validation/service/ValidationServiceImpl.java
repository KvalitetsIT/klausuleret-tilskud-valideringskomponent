package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.ClauseRepository;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepository;
import dk.kvalitetsit.itukt.validation.repository.entity.DrugEntity;
import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationResult;
import dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess;

import java.util.Optional;

public class ValidationServiceImpl implements ValidationService<ValidationInput, ValidationResult> {

    private final ClauseCache clauseCache;
    private final StamDataRepository stamDataRepository;

    private final Evaluator evaluator = new Evaluator();

    public ValidationServiceImpl(ClauseCache clauseCache, StamDataRepository stamDataRepository) {
        this.clauseCache = clauseCache;
        this.stamDataRepository = stamDataRepository;
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        Optional<DrugEntity> drug = stamDataRepository.findDrugById(validationInput.drugId());

        return new ValidationSuccess();
    }
}
