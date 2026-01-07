package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.common.model.ValidationFailed;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.validation.service.model.ValidationError;
import dk.kvalitetsit.itukt.validation.stamdata.repository.cache.Cache;
import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidationServiceImpl implements ValidationService<ValidationInput, List<ValidationError>> {

    private final ClauseService clauseCache;
    private final Cache<Long, DrugClause> drugClauseCache;
    private final SkippedValidationService skippedValidationService;
    private final Cache<Department.Identifier, Department> departmentCache;

    public ValidationServiceImpl(ClauseService clauseCache, Cache<Long, DrugClause> drugClauseCache, SkippedValidationService skippedValidationService, Cache<Department.Identifier, Department> departmentCache) {
        this.clauseCache = clauseCache;
        this.drugClauseCache = drugClauseCache;
        this.skippedValidationService = skippedValidationService;
        this.departmentCache = departmentCache;
    }

    @Override
    public List<ValidationError> validate(ValidationInput validationInput) {
        ValidationInput propagatedInput = assignDepartmentSpecialities(validationInput);

        createSkippedValidations(propagatedInput);

        Optional<DrugClause> drugClauseByDrugId = drugClauseCache.get(propagatedInput.drugId());

        return drugClauseByDrugId.map(stamData -> stamData.clauses().stream()
                .flatMap(sc -> validateStamDataClause(propagatedInput, sc).stream())
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
                        clause.error().code()
                );
            });
    }

    private ValidationInput assignDepartmentSpecialities(ValidationInput input) {
        ValidationInput.Actor updatedCreatedBy = updateActorSpecialities(input.createdBy());

        Optional<ValidationInput.Actor> updatedReportedBy = input.reportedBy()
                .map(this::updateActorSpecialities);

        return new ValidationInput(
                input.personId(),
                updatedCreatedBy,
                updatedReportedBy,
                input.skippedErrorCodes(),
                input.citizenAge(),
                input.drugId(),
                input.indicationCode(),
                input.existingDrugMedication()
        );
    }


    private ValidationInput.Actor updateActorSpecialities(ValidationInput.Actor actor) {
        return actor.department()
                .map(dept -> {
                    Set<Department.Speciality> specs = retrieveSpecialities(dept);
                    return new ValidationInput.Actor(
                            actor.id(),
                            actor.specialityCode(),
                            Optional.of(dept.withSpecialities(specs))
                    );
                })
                .orElse(actor);
    }

    private Set<Department.Speciality> retrieveSpecialities(Department department) {
        Set<Department.Speciality> specialitiesBySor = department.sor().flatMap(this.departmentCache::get).map(Department::specialities).orElse(Set.of());
        Set<Department.Speciality> specialitiesByShak = department.shak().flatMap(this.departmentCache::get).map(Department::specialities).orElse(Set.of());
        return Stream.concat(specialitiesBySor.stream(), specialitiesByShak.stream()).collect(Collectors.toSet());
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