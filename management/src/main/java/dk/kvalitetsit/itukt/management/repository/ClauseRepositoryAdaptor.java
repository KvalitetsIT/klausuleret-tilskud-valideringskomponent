package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClauseRepositoryAdaptor {

    private final ClauseRepository clauseRepository;
    private final Mapper<ClauseEntity, Clause> entityMapper;

    public ClauseRepositoryAdaptor(ClauseRepository clauseRepository, Mapper<ClauseEntity, Clause> entityMapper) {
        this.clauseRepository = clauseRepository;
        this.entityMapper = entityMapper;
    }

    public Clause create(ClauseInput clause) throws ServiceException {
        var createdClause = clauseRepository.create(clause);
        return entityMapper.map(createdClause);
    }

    public Optional<Clause> read(UUID id) throws ServiceException {
        return clauseRepository.read(id).map(entityMapper::map);
    }

    /**
     * Retrieves active clauses, including only the latest version of each clause.
     */
    public List<Clause> readLatestActive() throws ServiceException {
        return this.entityMapper.map(clauseRepository.readLatestActive());
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
