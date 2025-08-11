package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.model.ClauseEntity;

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
