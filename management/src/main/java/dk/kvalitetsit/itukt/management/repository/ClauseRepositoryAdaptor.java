package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseFullInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClauseRepositoryAdaptor {

    private final ClauseRepository clauseRepository;
    private final Mapper<ClauseEntity, Clause> entityMapper;
    private final Mapper<Expression, ExpressionEntity> expressionMapper;

    public ClauseRepositoryAdaptor(ClauseRepository clauseRepository, Mapper<ClauseEntity, Clause> entityMapper, Mapper<Expression, ExpressionEntity> expressionMapper) {
        this.clauseRepository = clauseRepository;
        this.entityMapper = entityMapper;
        this.expressionMapper = expressionMapper;
    }

    public Clause create(ClauseFullInput clauseInput) throws ServiceException {
        var expressionEntity = expressionMapper.map(clauseInput.expression());
        var clauseEntityInput = new ClauseEntityInput(clauseInput.name(), expressionEntity, clauseInput.errorMessage(), clauseInput.status(), clauseInput.validFrom());
        var createdClause = clauseRepository.create(clauseEntityInput);
        return entityMapper.map(createdClause);
    }

    public Optional<Clause> read(UUID id) throws ServiceException {
        return clauseRepository.read(id).map(entityMapper::map);
    }

    /**
     * Retrieves the current version of each clause. Excluding drafts.
     */
    public List<Clause> readCurrentVersions() throws ServiceException {
        return this.entityMapper.map(clauseRepository.readCurrentVersions());
    }

    /**
     * Retrieves the current version of a clause. Excluding drafts.
     */
    public Optional<Clause> readCurrentVersion(String name) throws ServiceException {
        return clauseRepository.readCurrentVersion(name).map(entityMapper::map);
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
