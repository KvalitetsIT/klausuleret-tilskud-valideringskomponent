package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.SkippedValidationRepository;
import dk.kvalitetsit.itukt.common.service.ClauseDrugCounter;
import dk.kvalitetsit.itukt.management.exceptions.InvalidInputException;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseQuery;
import dk.kvalitetsit.itukt.management.service.model.ClauseFullInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseUpdateInput;

import java.util.ArrayList;
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
        if (readCurrentDraft(clause.name()).isPresent()) {
            throw new InvalidInputException("A draft clause with name '%s' already exists".formatted(clause.name()));
        }
        var secondaryParentId = readCurrentNonDraft(clause.name()).map(Clause::id).orElse(null);
        String userID = userContextService.getUserID();
        var clauseFullInput = new ClauseFullInput(clause.name(), clause.expression(), clause.errorMessage(), Clause.Status.DRAFT, userID, null, secondaryParentId);
        return repository.create(clauseFullInput);
    }

    @Override
    public Optional<Clause> read(UUID id) {
        return repository.read(id);
    }

    @Override
    public List<Clause> readByStatus(Clause.Status status) {
        ClauseQuery query = new ClauseQuery().statuses(status);
        query = switch (status) {
            case ACTIVE, INACTIVE -> query.withoutPrimaryChildren();
            case DRAFT -> query.withoutChildren();
        };
        return repository.read(query);
    }

    @Override
    public List<Clause> readHistory(UUID uuid) {
        var history = new ArrayList<Clause>();
        var current = repository.read(uuid);
        while (current.isPresent()) {
            history.add(current.get());
            current = repository.readParent(current.get().uuid());
        }
        return history;
    }

    @Override
    public Clause approve(UUID clauseUuid, boolean resetSkippedValidations) throws ManagementException {
        Clause draft = repository.read(new ClauseQuery().statuses(Clause.Status.DRAFT).withoutChildren())
                .stream()
                .filter(clause -> clause.uuid().equals(clauseUuid))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Clause %s is not a current draft and can not be approved".formatted(clauseUuid)));
        Optional<Long> currentClauseId = readCurrentNonDraft(draft.name()).map(Clause::id);
        String userID = userContextService.getUserID();

        var clauseInput = new ClauseFullInput(draft.name(), draft.expression(), draft.error().message(), Clause.Status.ACTIVE, userID, currentClauseId.orElse(null), draft.id());
        Clause created = repository.create(clauseInput);

        if (!resetSkippedValidations && currentClauseId.isPresent()) {
            skippedValidationRepository.copySkippedValidation(currentClauseId.get(), created.id());
        }
        return created;
    }

    @Override
    public Clause inactivate(Clause.Name name) throws InvalidInputException {
        return updateStatus(name, Clause.Status.ACTIVE, "Only ACTIVE clauses can be inactivated", Clause.Status.INACTIVE);
    }

    @Override
    public Clause activate(Clause.Name name) throws InvalidInputException {
        return updateStatus(name, Clause.Status.INACTIVE, "Only INACTIVE clauses can be activated", Clause.Status.ACTIVE);
    }

    private Clause updateStatus(Clause.Name name, Clause.Status currentStatus, String errorMessage, Clause.Status nextStatus) throws InvalidInputException {
        var clause = readSingle(new ClauseQuery()
                .name(name.name())
                .statuses(currentStatus)
                .withoutPrimaryChildren())
                .orElseThrow(() -> new InvalidInputException(errorMessage));

        var clauseInput = new ClauseFullInput(clause.name(), clause.expression(), clause.error().message(), nextStatus, clause.createdBy(), clause.id(), null);
        Clause created = repository.create(clauseInput);
        skippedValidationRepository.copySkippedValidation(clause.id(), created.id());
        return created;
    }

    /**
     * Deletes the current draft versions of the clause matching the given uuid.
     * Draft versions are deleted up until the initial draft version.
     * I.e. the version that either has no parent clause, or a parent that is not a draft.
     *
     * @param uuid UUID of the current draft version.
     * @throws NotFoundException If the given UUID does not match a current draft version
     */
    @Override
    public Clause deleteDraft(UUID uuid) throws NotFoundException {
        var draft = repository.read(uuid)
                .flatMap(clause -> readCurrentDraft(clause.name()))
                .filter(clause -> clause.uuid().equals(uuid))
                .orElseThrow(() -> new NotFoundException("Clause %s is not a current draft and can not be deleted".formatted(uuid)));
        deleteDraft(draft.name());
        return draft;
    }

    private void deleteDraft(Clause.Name name) {
        readCurrentDraft(name)
                .map(draft -> {
                    try {
                        return repository.deleteDraft(draft.uuid());
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e); // Should never happen, since the draft was just read
                    }
                })
                .ifPresent(_ -> deleteDraft(name));
    }

    @Override
    public Clause updateDraft(Clause.Name name, ClauseUpdateInput clause) throws ManagementException {
        var currentDraft = readCurrentDraft(name)
                .orElseThrow(() -> new NotFoundException("No current draft found with name '%s'".formatted(name)));

        String userID = userContextService.getUserID();
        var clauseFullInput = new ClauseFullInput(name, clause.expression(), clause.errorMessage(), Clause.Status.DRAFT, userID, currentDraft.id(), null);
        return repository.create(clauseFullInput);
    }

    @Override
    public long getNumberOfDrugsForClause(Clause.Name name) {
        return clauseDrugCounter.getNumberOfDrugsForClause(name.name());
    }

    private Optional<Clause> readCurrentDraft(Clause.Name name) {
        return readSingle(new ClauseQuery()
                .name(name.name())
                .statuses(Clause.Status.DRAFT)
                .withoutChildren()
        );
    }

    private Optional<Clause> readCurrentNonDraft(Clause.Name name) {
        return readSingle(new ClauseQuery()
                .name(name.name())
                .statuses(Clause.Status.ACTIVE, Clause.Status.INACTIVE)
                .withoutPrimaryChildren()
        );
    }

    private Optional<Clause> readSingle(ClauseQuery query) {
        var currentClauses = repository.read(query);
        if (currentClauses.size() > 1) {
            throw new RuntimeException("Multiple clauses found for query: " + query);
        }
        return currentClauses.stream().findFirst();
    }
}
