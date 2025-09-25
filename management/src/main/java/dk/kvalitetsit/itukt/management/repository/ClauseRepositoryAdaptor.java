package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.entity.ClauseModel;
import dk.kvalitetsit.itukt.common.entity.State;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.entity.ClauseEntity;
import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClauseRepositoryAdaptor implements ClauseRepository<ClauseModel> {

    private final ClauseRepository<ClauseEntity> clauseRepository;
    private final Mapper<ClauseModel.NewClause, ClauseEntity.NewClause> newMapper;
    private final Mapper<ClauseEntity.PersistedClause, ClauseModel.PersistedClause> persistedMapper;

    public ClauseRepositoryAdaptor(ClauseRepository<ClauseEntity> clauseRepository, Mapper<ClauseModel.NewClause, ClauseEntity.NewClause> newMapper, Mapper<ClauseEntity.PersistedClause, ClauseModel.PersistedClause> persistedMapper) {
        this.clauseRepository = clauseRepository;
        this.newMapper = newMapper;
        this.persistedMapper = persistedMapper;
    }


    @Override
    public State.Persisted create(State.New entry) throws ServiceException {

    }

    @Override
    public Optional<State.Persisted> read(UUID id) throws ServiceException {
        return Optional.empty();
    }

    @Override
    public List<State.Persisted> readAll() throws ServiceException {
        return List.of();
    }
}
