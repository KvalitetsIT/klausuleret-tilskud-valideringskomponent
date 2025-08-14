package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.service.model.ValidationInput;
import dk.kvalitetsit.itukt.validation.service.model.ValidationResult;
import dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess;

public class ValidationServiceImpl implements ValidationService<ValidationInput, ValidationResult> {

    private final ClauseCache clauseCache;

    private final Evaluator evaluator = new Evaluator();

    public ValidationServiceImpl(ClauseCache clauseCache) {
        this.clauseCache = clauseCache;
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        return new ValidationSuccess();
    }
}
