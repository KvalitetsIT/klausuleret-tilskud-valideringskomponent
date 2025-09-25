package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.repository.StamDataCache;
import dk.kvalitetsit.itukt.validation.service.model.StamData;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;

import java.util.List;
import java.util.Optional;

public class ValidationServiceImpl implements ValidationService<ValidationInput, List<ValidationError>> {

    private final ClauseCache clauseCache;
    private final StamDataCache stamDataCache;

    public ValidationServiceImpl(ClauseCache clauseCache, StamDataCache stamDataCache) {
        this.clauseCache = clauseCache;
        this.stamDataCache = stamDataCache;
    }

    @Override
    public List<ValidationError> validate(ValidationInput validationInput) {
        Optional<StamData> stamDataByDrugId = stamDataCache.getStamDataByDrugId(validationInput.drugId());

        return stamDataByDrugId.map(stamData -> stamData.clauses().stream()
                .map(sc -> validateStamDataClause(validationInput, sc))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList()).orElseGet(List::of);
    }

    private Optional<ValidationError> validateClause(Clause clause, String clauseText, ValidationInput validationInput) {
        return clause.expression().validates(validationInput) ?
                Optional.empty() :
                Optional.of(new ValidationError(clause.name(), clauseText, "TODO: IUAKT-76", clause.errorCode()));
    }

    private Optional<ValidationError> validateStamDataClause(ValidationInput validationInput, StamData.Clause clause) {
        return clauseCache.getClause(clause.code())
                .flatMap(c -> validateClause(c, clause.text(), validationInput));

    }


}

