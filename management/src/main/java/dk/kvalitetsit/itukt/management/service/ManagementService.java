package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.entity.CRUD;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.entity.ClauseEntity;

public interface ManagementService<T> extends CRUD<ClauseEntity.NewClause, ClauseEntity.PersistedClause, Clause> {
}
