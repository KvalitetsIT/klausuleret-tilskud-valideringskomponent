package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
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

    public Clause create(ClauseFullInput clauseInput) throws ServiceException {
        var clauseEntityInput = clauseInputMapper.map(clauseInput);
        var createdClause = clauseRepository.create(clauseEntityInput);
        return entityMapper.map(createdClause);
    }

    public Optional<Clause> read(UUID id) throws ServiceException {
        return clauseRepository.read(id).map(entityMapper::map);
    }

    /**
     * Retrieves the current version of each clause. Excluding drafts.
     */
    public List<Clause> readCurrentClauses() throws ServiceException {
        return this.entityMapper.map(clauseRepository.readCurrentClauses());
    }

    /**
     * Retrieves the current version of a clause. Excluding drafts.
     */
    public Optional<Clause> readCurrentClause(String name) throws ServiceException {
        return clauseRepository.readCurrentClause(name).map(entityMapper::map);
    }

    public List<Clause> readAllDrafts() throws ServiceException {
        return this.entityMapper.map(clauseRepository.readAllDrafts());
    }


    public List<Clause> readHistory(String name) throws ServiceException {
        return this.entityMapper.map(clauseRepository.readHistory(name));
    }

    public void updateDraftToActive(UUID uuid) throws ServiceException {
        clauseRepository.updateDraftToActive(uuid);
    }
}
