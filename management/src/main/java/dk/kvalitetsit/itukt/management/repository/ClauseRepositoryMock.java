package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.common.repository.ClauseRepository;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClauseRepositoryMock implements ClauseRepository<ClauseEntity> {

    private final ClauseEntity clause = new ClauseEntity(1L, UUID.randomUUID(), "", new ExpressionEntity.ConditionEntity(2L, "field", Operator.EQUAL, List.of("")));

    @Override
    public Optional<ClauseEntity> create(ClauseEntity entry) throws ServiceException {
        return Optional.of(clause);
    }

    @Override
    public Optional<ClauseEntity> read(UUID id) throws ServiceException {
        if (!id.equals(clause.uuid())) return Optional.empty();
        return Optional.of(clause);
    }

    @Override
    public List<ClauseEntity> readAll() throws ServiceException {
        return List.of(clause);
    }
}
