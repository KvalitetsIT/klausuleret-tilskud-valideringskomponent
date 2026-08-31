package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import dk.kvalitetsit.itukt.management.service.model.ClauseUpdateInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagementService {
    Clause create(ClauseInput clause) throws ManagementException;

    default Clause create(ClauseInput clause, boolean skipValidation) throws ManagementException {
        return create(clause);
    }

    Optional<Clause> read(UUID id);

    List<Clause> readByStatus(Clause.Status status);

    List<Clause> readHistory(UUID uuid);

    Clause approve(UUID clauseUuid, boolean skipValidation) throws ManagementException;

    Clause inactivate(String name) throws ManagementException;

    Clause activate(String name) throws ManagementException;

    Clause deleteDraft(UUID id) throws ManagementException;

    Clause updateDraft(String name, ClauseUpdateInput clause) throws ManagementException;

    default Clause updateDraft(String name, ClauseUpdateInput clause, boolean skipValidation) throws ManagementException {
        return updateDraft(name, clause);
    }

    long getNumberOfDrugsForClause(String name);
}
