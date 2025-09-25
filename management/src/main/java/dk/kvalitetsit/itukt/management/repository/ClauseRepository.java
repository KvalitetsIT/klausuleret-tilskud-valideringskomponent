package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.entity.CRUD;
import dk.kvalitetsit.itukt.common.entity.ClauseEntity;
import dk.kvalitetsit.itukt.common.entity.State;
import dk.kvalitetsit.itukt.common.model.Clause;

public interface ClauseRepository<T extends State<Clause>> extends CRUD<ClauseEntity.New, ClauseEntity.Persisted, Clause> {
}
