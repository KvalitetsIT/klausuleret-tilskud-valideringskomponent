package dk.kvalitetsit.itukt.common.service;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.Optional;

public interface ClauseService {

    Optional<Clause.Persisted> get(String name);

    Optional<Clause.Persisted> getByErrorCode(Integer errorCode);
}
