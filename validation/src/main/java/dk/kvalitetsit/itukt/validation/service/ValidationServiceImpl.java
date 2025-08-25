package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataCache;
import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationResult;
import dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess;

import java.util.Optional;

public class ValidationServiceImpl implements ValidationService<ValidationInput, ValidationResult> {

    private final ClauseCache clauseCache;
    private final StamDataCache stamDataCache;

    private final Evaluator evaluator = new Evaluator();

    public ValidationServiceImpl(ClauseCache clauseCache, StamDataCache stamDataCache) {
        this.clauseCache = clauseCache;
        this.stamDataCache = stamDataCache;
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        Optional<String> clauseName = stamDataCache.getClauseNameByDrugId(validationInput.drugId());

        return new ValidationSuccess();
    }
}
