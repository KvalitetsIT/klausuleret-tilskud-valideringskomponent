package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity create(ClauseInput clause) throws ServiceException;
    Optional<ClauseEntity> read(UUID uuid) throws ServiceException;
    boolean nameExists(String name) throws ServiceException;
    /**
     * Retrieves active clauses, including only the latest version of each clause.
     */
    List<ClauseEntity> readAllActive() throws ServiceException;
    List<ClauseEntity> readAllDrafts() throws ServiceException;

    List<ClauseEntity> readHistory(String name);

    /**
     * Updates the clause with the given ID from DRAFT status to ACTIVE status.
     * @throws NotFoundException if the clause cannot be found or if it is not in DRAFT status.
     */
    void updateDraftToActive(UUID uuid) throws NotFoundException;
}
