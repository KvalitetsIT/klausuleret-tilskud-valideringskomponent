package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagementService {
    Clause create(ClauseInput clause) throws ManagementException;

    Optional<Clause> read(UUID id);

    List<Clause> readByStatus(Clause.Status status);

    List<Clause> readHistory(String name) throws ManagementException;

    Clause approve(UUID clauseUuid, boolean skipValidation) throws ManagementException;

    Clause inactivate(String name) throws ManagementException;

    Clause activate(String name) throws ManagementException;

    Clause deleteDraft(UUID id) throws ManagementException;

    long getNumberOfDrugsForClause(String name);
}
