package dk.kvalitetsit.itukt.common.entity;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.UUID;

public sealed interface ClauseModel extends State<Clause> permits ClauseModel.NewClause, ClauseModel.PersistedClause  {

    record PersistedClause(Long id, UUID uuid) implements ClauseModel, WithUuid, WithId {}

    record NewClause() implements ClauseModel {}
}
