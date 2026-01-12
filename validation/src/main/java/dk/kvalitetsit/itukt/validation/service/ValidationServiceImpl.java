package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.ValidationFailed;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.Cache;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;

import java.util.List;
import java.util.Optional;

public class ValidationServiceImpl implements ValidationService<ValidationInput, List<ValidationError>> {

    private final ClauseService clauseCache;
    private final Cache<Long, DrugClause> drugClauseCache;
    private final SkippedValidationService skippedValidationService;

    public ValidationServiceImpl(ClauseService clauseCache, Cache<Long, DrugClause> drugClauseCache, SkippedValidationService skippedValidationService) {
        this.clauseCache = clauseCache;
        this.drugClauseCache = drugClauseCache;
        this.skippedValidationService = skippedValidationService;
    }

    @Override
    public List<ValidationError> validate(ValidationInput validationInput) {
        createSkippedValidations(validationInput);

        Optional<DrugClause> drugClauseByDrugId = drugClauseCache.get(validationInput.drugId());

        return drugClauseByDrugId.map(stamData -> stamData.clauses().stream()
                .flatMap(sc -> validateStamDataClause(validationInput, sc).stream())
                .toList()).orElseGet(List::of);
    }

    private Optional<ValidationError> validateClause(Clause clause, String clauseText, ValidationInput validationInput) {
        if (shouldSkipClause(clause, validationInput))
            return Optional.empty();
        else
            return clause.expression().validates(validationInput).map(validationFailed -> switch (validationFailed) {
                case ValidationFailed.ExistingDrugMedicationRequired ignored ->
                        throw new ExistingDrugMedicationRequiredException();
                case dk.kvalitetsit.itukt.common.model.ValidationError error -> new ValidationError(
                        new ValidationError.Clause(clause.name(), clauseText, clause.error().message()),
                        error.toErrorString(),
                        clause.error().code(),
                        validationInput.elementPath()
                );
            });
    }

    private void createSkippedValidations(ValidationInput validationInput) {
        skippedValidationService.createSkippedValidations(validationInput.createdBy().id(), validationInput.personId(), validationInput.skippedErrorCodes());
        validationInput.reportedBy().ifPresent(reportedBy -> skippedValidationService.createSkippedValidations(reportedBy.id(), validationInput.personId(), validationInput.skippedErrorCodes()));
    }

    private Optional<ValidationError> validateStamDataClause(ValidationInput validationInput, DrugClause.Clause clause) {
        return clauseCache.get(clause.code())
                .flatMap(c -> validateClause(c, clause.text(), validationInput));
    }

    private boolean shouldSkipClause(Clause clause, ValidationInput validationInput) {
        return skippedValidationService.shouldSkipValidation(validationInput.createdBy().id(), validationInput.personId(), clause.id()) ||
                validationInput.reportedBy()
                        .map(reportedBy -> skippedValidationService.shouldSkipValidation(reportedBy.id(), validationInput.personId(), clause.id()))
                        .orElse(false);
    }
}