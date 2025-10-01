package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;

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

    public Clause create(ClauseForCreation clause) throws ServiceException {
        var createdClause = clauseRepository.create(clause);
        return entityMapper.map(createdClause);
    }

    public Optional<Clause> read(UUID id) throws ServiceException {
        return clauseRepository.read(id).map(entityMapper::map);
    }

    public List<Clause> readAll() throws ServiceException {
        return this.entityMapper.map(clauseRepository.readAll());
    }

    public List<Long> getClauseIdsByErrorCodes(List<Integer> errorCodes) throws ServiceException {
        return clauseRepository.getClauseIdsByErrorCodes(errorCodes);
    }
}
