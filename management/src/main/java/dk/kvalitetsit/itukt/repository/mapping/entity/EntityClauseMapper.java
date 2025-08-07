package dk.kvalitetsit.itukt.repository.mapping.entity;

import dk.kvalitetsit.itukt.Mapper;
import dk.kvalitetsit.itukt.model.Clause;
import dk.kvalitetsit.itukt.repository.model.ClauseEntity;

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
