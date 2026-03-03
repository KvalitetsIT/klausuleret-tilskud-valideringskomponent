package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.exceptions.BadRequestException;
import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.service.model.ClauseFullInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManagementServiceImpl implements ManagementService {

    private final ClauseRepositoryAdaptor repository;
    private final SkippedValidationRepository skippedValidationRepository;

    public ManagementServiceImpl(ClauseRepositoryAdaptor repository, SkippedValidationRepository skippedValidationRepository) {
        this.repository = repository;
        this.skippedValidationRepository = skippedValidationRepository;
    }

    @Override
    public Clause create(ClauseInput clause) throws ServiceException {
        var clauseFullInput = new ClauseFullInput(clause.name(), clause.expression(), clause.errorMessage(), Clause.Status.DRAFT, null);
        return repository.create(clauseFullInput);
    }

    @Override
    public Optional<Clause> read(UUID id) throws ServiceException {
        return repository.read(id);
    }

    @Override
    public List<Clause> readByStatus(Clause.Status status) throws ServiceException {
        return switch (status) {
            case ACTIVE, INACTIVE -> getLatestClauseVersions(status);
            case DRAFT -> repository.readAllDrafts();
        };
    }

    private List<Clause> getLatestClauseVersions(Clause.Status status) {
        return repository.readCurrentClauses().stream()
                .filter(clause -> clause.status() == status).toList();
    }

    @Override
    public List<Clause> readHistory(String name) throws ServiceException {
        List<Clause> history = repository.readHistory(name);
        if (history.isEmpty())
            throw new NotFoundException(String.format("clause with name '%s' was not found", name));
        return history;
    }

    @Override
    public Clause approve(UUID clauseUuid, boolean resetSkippedValidations) throws ServiceException {
        Clause draft = repository.read(clauseUuid)
                .orElseThrow(() -> new NotFoundException("The clause associated with the given id was not found"));

        Optional<Clause> currentClause = repository.readCurrentClause(draft.name());

        if (!resetSkippedValidations && currentClause.isPresent()) {
            skippedValidationRepository.copySkippedValidation(currentClause.get().id(), draft.id());
        }
        return repository.updateDraftToActive(draft.uuid());
    }

    @Override
    public Clause inactivate(String name) throws ServiceException {
        return getClause(name, Clause.Status.ACTIVE, "Only ACTIVE clauses can be inactivated", Clause.Status.INACTIVE);
    }

    @Override
    public Clause activate(String name) throws ServiceException {
        return getClause(name, Clause.Status.INACTIVE, "Only INACTIVE clauses can be activated", Clause.Status.ACTIVE);
    }

    private Clause getClause(String name, Clause.Status currentStatus, String errorMessage, Clause.Status nextStatus) {
        var clause = repository.readCurrentClause(name)
                .filter(c -> c.status() == currentStatus)
                .orElseThrow(() -> new BadRequestException(errorMessage));

        var clauseInput = new ClauseFullInput(clause.name(), clause.expression(), clause.error().message(), nextStatus, new Date());
        Clause created = repository.create(clauseInput);
        skippedValidationRepository.copySkippedValidation(clause.id(), created.id());
        return created;
    }
}
