package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseFullInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClauseRepositoryAdaptor {

    private final ClauseRepository clauseRepository;
    private final Mapper<ClauseEntity, Clause> entityMapper;
    private final Mapper<ClauseFullInput, ClauseEntityInput> clauseInputMapper;

    public ClauseRepositoryAdaptor(ClauseRepository clauseRepository, Mapper<ClauseEntity, Clause> entityMapper, Mapper<ClauseFullInput, ClauseEntityInput> clauseInputMapper) {
        this.clauseRepository = clauseRepository;
        this.entityMapper = entityMapper;
        this.clauseInputMapper = clauseInputMapper;
    }

    public Clause create(ClauseFullInput clauseInput) {
        var clauseEntityInput = clauseInputMapper.map(clauseInput);
        var createdClause = clauseRepository.create(clauseEntityInput);
        return entityMapper.map(createdClause);
    }

    public Optional<Clause> read(UUID id) {
        return clauseRepository.read(id).map(entityMapper::map);
    }

    /**
     * Retrieves the current version of each clause. Excluding drafts.
     */
    public List<Clause> readCurrentNonDraftClauses() {
        return this.entityMapper.map(clauseRepository.readCurrentNonDraftClauses());
    }

    /**
     * Retrieves the current version of a clause. Excluding drafts.
     */
    public Optional<Clause> readCurrentNonDraftClause(String name) {
        return clauseRepository.readCurrentNonDraftClause(name).map(entityMapper::map);
    }

    /**
     * Retrieves the draft clause with no child clauses, if such exist.
     */
    public Optional<Clause> readCurrentDraft(String name) {
        return clauseRepository.readCurrentDraft(name).map(entityMapper::map);
    }

    /**
     * Retrieves all draft clauses that have no child clauses.
     */
    public List<Clause> readCurrentDrafts() {
        return this.entityMapper.map(clauseRepository.readCurrentDrafts());
    }


    public List<Clause> readHistory(String name) {
        return this.entityMapper.map(clauseRepository.readHistory(name));
    }

    public Clause deleteDraft(UUID id) throws NotFoundException {
        return this.entityMapper.map(clauseRepository.deleteDraft(id));
    }
}
