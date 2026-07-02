package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagementService {
    Clause create(ClauseInput clause);

    Optional<Clause> read(UUID id);

    List<Clause> readByStatus(Clause.Status status);

    List<Clause> readHistory(String name) throws NotFoundException;

    Clause approve(UUID clauseUuid, boolean skipValidation) throws NotFoundException;

    Clause inactivate(String name);

    Clause activate(String name);

    Clause deleteDraft(UUID id) throws NotFoundException;

    long getNumberOfDrugsForClause(String name);
}
