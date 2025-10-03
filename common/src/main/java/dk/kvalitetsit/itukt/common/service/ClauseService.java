package dk.kvalitetsit.itukt.common.service;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.Optional;

public interface ClauseService {

    Optional<Clause> get(String name);

}
