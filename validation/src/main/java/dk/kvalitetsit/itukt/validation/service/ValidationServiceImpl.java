package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.repository.cache.StamdataCache;
import dk.kvalitetsit.itukt.validation.service.model.StamData;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;

import java.util.List;
import java.util.Optional;

public class ValidationServiceImpl implements ValidationService<ValidationInput, List<ValidationError>> {

    private final ClauseService clauseCache;
    private final StamdataCache stamDataCache;
    private final SkippedValidationService skippedValidationService;

    public ValidationServiceImpl(ClauseService clauseCache, StamdataCache stamDataCache, SkippedValidationService skippedValidationService) {
        this.clauseCache = clauseCache;
        this.stamDataCache = stamDataCache;
        this.skippedValidationService = skippedValidationService;
    }

    @Override
    public List<ValidationError> validate(ValidationInput validationInput) {
        createSkippedValidations(validationInput);

        Optional<StamData> stamDataByDrugId = stamDataCache.get(validationInput.drugId());

        return stamDataByDrugId.map(stamData -> stamData.clauses().stream()
                .flatMap(sc -> validateStamDataClause(validationInput, sc).stream())
                .toList()).orElseGet(List::of);
    }

    private void createSkippedValidations(ValidationInput validationInput) {
        skippedValidationService.createSkippedValidations(validationInput.createdById(), validationInput.personId(), validationInput.skippedErrorCodes());
        validationInput.reportedById().ifPresent(reportedBy -> skippedValidationService.createSkippedValidations(reportedBy, validationInput.personId(), validationInput.skippedErrorCodes()));
    }

    private Optional<ValidationError> validateStamDataClause(ValidationInput validationInput, StamData.Clause clause) {
        return clauseCache.get(clause.code())
                .flatMap(c -> validateClause(c, clause.text(), validationInput));
    }

    private Optional<ValidationError> validateClause(Clause clause, String clauseText, ValidationInput validationInput) {
        return shouldSkipClause(clause, validationInput) || clause.expression().validates(validationInput) ?
                Optional.empty() :
                Optional.of(new ValidationError(clause.name(), clauseText, clause.error().message(), clause.error().code()));
    }

    private boolean shouldSkipClause(Clause clause, ValidationInput validationInput) {
        return skippedValidationService.shouldSkipValidation(validationInput.createdById(), validationInput.personId(), clause.id()) ||
                (validationInput.reportedById().isPresent() && skippedValidationService.shouldSkipValidation(validationInput.reportedById().get(), validationInput.personId(), clause.id()));
    }
}
