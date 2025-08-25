package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataCache;
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
        Optional<Clause> clause = stamDataCache.getClauseCodeByDrugId(validationInput.drugId())
                .flatMap(clauseCache::getClause);

        return clause.map(c -> validateClause(c, validationInput)).orElse(new ValidationSuccess());
    }

    private ValidationResult validateClause(Clause clause, ValidationInput validationInput) {
        DataContext dataContext = validationDataContextMapper.map(validationInput);
        boolean success = evaluator.eval(clause.expression(), dataContext);
        return success ? new ValidationSuccess() : new ValidationError(clause.name(), "Validation failed");
    }
}
