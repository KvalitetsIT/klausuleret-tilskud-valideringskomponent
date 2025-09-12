package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataCache;
import dk.kvalitetsit.itukt.validation.repository.entity.ClauseEntity;
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
        return stamDataCache.getClauseByDrugId(validationInput.drugId())
                .flatMap(stamDataClause -> validateStamDataClause(validationInput, stamDataClause))
                .orElse(new ValidationSuccess());
    }

    private Optional<ValidationResult> validateStamDataClause(ValidationInput validationInput, ClauseEntity stamDataClause) {
        return clauseCache.getClause(stamDataClause.kode())
                .map(clause -> validateClause(clause, stamDataClause.tekst(), validationInput));
    }

    private ValidationResult validateClause(Clause clause, String clauseText, ValidationInput validationInput) {
        boolean success = clause.expression().validates(validationInput);
        return success ? new ValidationSuccess() : new ValidationError(clause.name(), clauseText, "TODO: IUAKT-76");
    }
}
