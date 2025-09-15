package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.StamdataEntity;
import dk.kvalitetsit.itukt.common.repository.cache.ClauseCache;
import dk.kvalitetsit.itukt.common.repository.cache.StamdataCache;
import dk.kvalitetsit.itukt.validation.service.model.*;

import java.util.Optional;

public class ValidationServiceImpl implements ValidationService<ValidationInput, ValidationResult> {

    private final ClauseCache clauseCache;
    private final StamdataCache stamdataCache;
    private final Mapper<ValidationInput, DataContext> validationDataContextMapper;

    private final Evaluator evaluator;

    public ValidationServiceImpl(ClauseCache clauseCache, StamdataCache stamdataCache, Mapper<ValidationInput, DataContext> validationDataContextMapper, Evaluator evaluator) {
        this.clauseCache = clauseCache;
        this.stamdataCache = stamdataCache;
        this.validationDataContextMapper = validationDataContextMapper;
        this.evaluator = evaluator;
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        return stamdataCache.get(validationInput.drugId())
                .flatMap(stamdata -> stamdata.clause().stream()
                        .map(c -> validateStamDataClause(validationInput, c))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst())
                .orElse(new ValidationSuccess());
    }

    private Optional<ValidationResult> validateStamDataClause(ValidationInput validationInput, StamdataEntity.Clause stamDataClause) {
        return clauseCache.get(stamDataClause.code())
                .map(clause -> validateClause(clause, stamDataClause.text(), validationInput));
    }

    private ValidationResult validateClause(Clause clause, String clauseText, ValidationInput validationInput) {
        DataContext dataContext = validationDataContextMapper.map(validationInput);
        boolean success = evaluator.eval(clause.expression(), dataContext);
        return success ? new ValidationSuccess() : new ValidationError(clause.name(), clauseText, "TODO: IUAKT-76");
    }
}
