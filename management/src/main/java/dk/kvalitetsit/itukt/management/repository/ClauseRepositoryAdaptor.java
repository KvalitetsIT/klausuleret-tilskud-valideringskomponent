package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClauseRepositoryAdaptor {

    private final ClauseRepository clauseRepository;
    private final Mapper<ClauseEntity.Persisted, Clause.Persisted> entityMapper;
    private final Mapper<Clause.NotPersisted, ClauseEntity.NotPersisted> notPersistedMapper;

    public ClauseRepositoryAdaptor(ClauseRepository clauseRepository, Mapper<ClauseEntity.Persisted, Clause.Persisted> entityMapper, Mapper<Clause.NotPersisted, ClauseEntity.NotPersisted> notPersistedMapper) {
        this.clauseRepository = clauseRepository;
        this.entityMapper = entityMapper;
        this.notPersistedMapper = notPersistedMapper;
    }

    public Clause.Persisted create(Clause.NotPersisted clause) throws ServiceException {
        var createdClause = clauseRepository.create(notPersistedMapper.map(clause));
        return entityMapper.map(createdClause);
    }

    public Optional<Clause.Persisted> read(UUID id) throws ServiceException {
        return clauseRepository.read(id).map(entityMapper::map);
    }

    public List<Clause.Persisted> readAll() throws ServiceException {
        return this.entityMapper.map(clauseRepository.readAll());
    }
}
