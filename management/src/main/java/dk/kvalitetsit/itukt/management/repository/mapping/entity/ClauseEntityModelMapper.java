package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class ClauseEntityModelMapper implements Mapper<ClauseEntity, Clause> {
    private final ExpressionEntityModelMapper expressionEntityModelMapper;

    public ClauseEntityModelMapper(ExpressionEntityModelMapper expressionEntityModelMapper) {
        this.expressionEntityModelMapper = expressionEntityModelMapper;
    }

    @Override
    public Clause map(ClauseEntity entry) {
        return new Clause(entry.id(), entry.name(), entry.uuid(), new Clause.Error(entry.errorMessage(), entry.errorCode()), expressionEntityModelMapper.map(entry.expression()), entry.createdAt());
    }
}