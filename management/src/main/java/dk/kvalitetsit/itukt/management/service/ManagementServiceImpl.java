package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.common.service.ClauseDrugCounter;
import dk.kvalitetsit.itukt.management.exceptions.InvalidInputException;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.service.model.ClauseFullInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManagementServiceImpl implements ManagementService {

    private final ClauseRepositoryAdaptor repository;
    private final SkippedValidationRepository skippedValidationRepository;
    private final UserContextService userContextService;
    private final ClauseDrugCounter clauseDrugCounter;

    public ManagementServiceImpl(ClauseRepositoryAdaptor repository, SkippedValidationRepository skippedValidationRepository, UserContextService userContextService, ClauseDrugCounter clauseDrugCounter) {
        this.repository = repository;
        this.skippedValidationRepository = skippedValidationRepository;
        this.userContextService = userContextService;
        this.clauseDrugCounter = clauseDrugCounter;
    }

    @Override
    public Clause create(ClauseInput clause) throws InvalidInputException {
        if (repository.readCurrentDraft(clause.name()).isPresent()) {
            throw new InvalidInputException("A draft clause with name '%s' already exists".formatted(clause.name()));
        }
        var parentClauseId = repository.readCurrentNonDraftClause(clause.name()).map(Clause::id).orElse(null);
        String userID = userContextService.getUserID();
        var clauseFullInput = new ClauseFullInput(clause.name(), clause.expression(), clause.errorMessage(), Clause.Status.DRAFT, userID, parentClauseId);
        return repository.create(clauseFullInput);
    }

    @Override
    public Optional<Clause> read(UUID id) {
        return repository.read(id);
    }

    @Override
    public List<Clause> readByStatus(Clause.Status status) {
        return switch (status) {
            case ACTIVE, INACTIVE -> getLatestClauseVersions(status);
            case DRAFT -> repository.readCurrentDrafts();
        };
    }

    private List<Clause> getLatestClauseVersions(Clause.Status status) {
        return repository.readCurrentNonDraftClauses().stream()
                .filter(clause -> clause.status() == status).toList();
    }

    @Override
    public List<Clause> readHistory(String name) throws NotFoundException {
        List<Clause> history = repository.readHistory(name);
        if (history.isEmpty())
            throw new NotFoundException(String.format("clause with name '%s' was not found", name));
        return history;
    }

    @Override
    public Clause approve(UUID clauseUuid, boolean resetSkippedValidations) throws ManagementException {
        Clause draft = repository.readCurrentDrafts()
                .stream()
                .filter(clause -> clause.uuid().equals(clauseUuid))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Clause %s is not a current draft and can not be approved".formatted(clauseUuid)));
        Optional<Clause> currentClause = repository.readCurrentNonDraftClause(draft.name());
        String userID = userContextService.getUserID();

        var clauseInput = new ClauseFullInput(draft.name(), draft.expression(), draft.error().message(), Clause.Status.ACTIVE, userID, draft.id());
        Clause created = repository.create(clauseInput);

        if (!resetSkippedValidations && currentClause.isPresent()) {
            skippedValidationRepository.copySkippedValidation(currentClause.get().id(), created.id());
        }
        return created;
    }

    @Override
    public Clause inactivate(String name) throws InvalidInputException {
        return updateStatus(name, Clause.Status.ACTIVE, "Only ACTIVE clauses can be inactivated", Clause.Status.INACTIVE);
    }

    @Override
    public Clause activate(String name) throws InvalidInputException {
        return updateStatus(name, Clause.Status.INACTIVE, "Only INACTIVE clauses can be activated", Clause.Status.ACTIVE);
    }

    private Clause updateStatus(String name, Clause.Status currentStatus, String errorMessage, Clause.Status nextStatus) throws InvalidInputException {
        var clause = repository.readCurrentNonDraftClause(name)
                .filter(c -> c.status() == currentStatus)
                .orElseThrow(() -> new InvalidInputException(errorMessage));

        var clauseInput = new ClauseFullInput(clause.name(), clause.expression(), clause.error().message(), nextStatus, clause.createdBy(), clause.id());
        Clause created = repository.create(clauseInput);
        skippedValidationRepository.copySkippedValidation(clause.id(), created.id());
        return created;
    }

    @Override
    public Clause deleteDraft(UUID id) throws NotFoundException {
        return repository.deleteDraft(id);
    }

    @Override
    public long getNumberOfDrugsForClause(String name) {
        return clauseDrugCounter.getNumberOfDrugsForClause(name);
    }
}
