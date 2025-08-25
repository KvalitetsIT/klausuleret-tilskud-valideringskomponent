package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataCache;
import dk.kvalitetsit.itukt.validation.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.validation.service.model.*;

import java.util.Optional;

public class ValidationServiceImpl implements ValidationService<ValidationInput, ValidationResult> {

    private final ClauseCache clauseCache;
    private final StamDataCache stamDataCache;
    private final Mapper<ValidationInput, DataContext> validationDataContextMapper;

    private final Evaluator evaluator;

    public ValidationServiceImpl(ClauseCache clauseCache, StamDataCache stamDataCache, Mapper<ValidationInput, DataContext> validationDataContextMapper, Evaluator evaluator) {
        this.clauseCache = clauseCache;
        this.stamDataCache = stamDataCache;
        this.validationDataContextMapper = validationDataContextMapper;
        this.evaluator = evaluator;
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
        DataContext dataContext = validationDataContextMapper.map(validationInput);
        boolean success = evaluator.eval(clause.expression(), dataContext);
        return success ? new ValidationSuccess() : new ValidationError(clause.name(), clauseText, "Validation failed");
    }
}
