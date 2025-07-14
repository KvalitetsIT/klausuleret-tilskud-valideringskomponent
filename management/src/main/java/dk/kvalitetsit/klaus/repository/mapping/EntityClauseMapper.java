package dk.kvalitetsit.klaus.repository.mapping;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.model.ClauseEntity;

import java.util.Optional;

public class EntityClauseMapper implements Mapper<ClauseEntity, Clause> {
    private final EntityExpressionMapper entityExpressionMapper;

    public EntityClauseMapper(EntityExpressionMapper entityExpressionMapper) {
        this.entityExpressionMapper = entityExpressionMapper;
    }

    @Override
    public Clause map(ClauseEntity entry) {
        return new Clause(entry.name(), Optional.of(entry.uuid()), entityExpressionMapper.map(entry.expression()));
    }
}
