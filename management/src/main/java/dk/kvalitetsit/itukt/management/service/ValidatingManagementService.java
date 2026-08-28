package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.exceptions.ExpressionValidationException;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseUpdateInput;
import dk.kvalitetsit.itukt.management.service.validator.ExpressionValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ValidatingManagementService {
    private final ManagementService managementService;
    private final ExpressionValidator<Expression> expressionValidator;

    public ValidatingManagementService(ManagementService managementService, ExpressionValidator<Expression> expressionValidator) {
        this.managementService = managementService;
        this.expressionValidator = expressionValidator;
    }

    public Clause create(ClauseInput clause, boolean skipValidation) throws ManagementException {
        if (!skipValidation) {
            validate(clause.expression());
        }
        return managementService.create(clause);
    }

    public Optional<Clause> read(UUID id) {
        return managementService.read(id);
    }

    public List<Clause> readByStatus(Clause.Status status) {
        return managementService.readByStatus(status);
    }

    public List<Clause> readHistory(UUID uuid) {
        return managementService.readHistory(uuid);
    }

    public Clause approve(UUID clauseUuid, boolean skipValidation) throws ManagementException {
        return managementService.approve(clauseUuid, skipValidation);
    }

    public Clause inactivate(String name) throws ManagementException {
        return managementService.inactivate(name);
    }

    public Clause activate(String name) throws ManagementException {
        return managementService.activate(name);
    }

    public Clause deleteDraft(UUID id) throws ManagementException {
        return managementService.deleteDraft(id);
    }

    public Clause updateDraft(String name, ClauseUpdateInput clause, boolean skipValidation) throws ManagementException {
        if (!skipValidation) {
            validate(clause.expression());
        }
        return managementService.updateDraft(name, clause);
    }

    public long getNumberOfDrugsForClause(String name) {
        return managementService.getNumberOfDrugsForClause(name);
    }

    private void validate(Expression expression) throws ExpressionValidationException {
        var validationErrors = expressionValidator.validate(expression);
        if(!validationErrors.isEmpty()) {
            throw new ExpressionValidationException(validationErrors);
        }
    }
}
