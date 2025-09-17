package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClauseRepositoryAdaptor {

    private final ClauseRepository clauseRepository;
    private final Mapper<Expression, ExpressionEntity> expressionMapper;
    private final Mapper<ClauseEntity, Clause> entityMapper;

    public ClauseRepositoryAdaptor(ClauseRepository clauseRepository, Mapper<Expression, ExpressionEntity> expressionMapper, Mapper<ClauseEntity, Clause> entityMapper) {
        this.clauseRepository = clauseRepository;
        this.expressionMapper = expressionMapper;
        this.entityMapper = entityMapper;
    }

    public Clause create(Clause entry) throws ServiceException {
        var expression = expressionMapper.map(entry.expression());
        var createdClause = clauseRepository.create(entry.name(), expression);
        return entityMapper.map(createdClause);
    }

    public Optional<Clause> read(UUID id) throws ServiceException {
        return clauseRepository.read(id).map(entityMapper::map);
    }

    public List<Clause> readAll() throws ServiceException {
        return clauseRepository.readAll().stream().map(this.entityMapper::map).toList();
    }
}
