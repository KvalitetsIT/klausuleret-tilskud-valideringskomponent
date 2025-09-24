package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataCache;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;
import dk.kvalitetsit.itukt.validation.service.model.ValidationResult;
import dk.kvalitetsit.itukt.validation.service.model.ValidationSuccess;

import java.util.Optional;

public class ValidationServiceImpl implements ValidationService<ValidationInput, ValidationResult> {

    private final ClauseCache clauseCache;
    private final StamDataCache stamDataCache;

    public ValidationServiceImpl(ClauseCache clauseCache, StamDataCache stamDataCache) {
        this.clauseCache = clauseCache;
        this.stamDataCache = stamDataCache;
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        return stamDataCache.getStamDataByDrugId(validationInput.drugId())
                .flatMap(stamdata -> stamdata.clause().stream()
                        .map(sc -> clauseCache.getClause(sc.code()).map(c -> validateClause(c, sc.text(), validationInput)))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(x -> x instanceof ValidationError)
                        .findFirst())
                .orElse(new ValidationSuccess());
    }

    private ValidationResult validateClause(Clause clause, String clauseText, ValidationInput validationInput) {
        return clause.expression().validates(validationInput) ?
                new ValidationSuccess() :
                new ValidationError(clause.name(), clauseText, "TODO: IUAKT-76", clause.errorCode());
    }
}

