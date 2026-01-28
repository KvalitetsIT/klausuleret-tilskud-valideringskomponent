package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity create(ClauseInput clause) throws ServiceException;
    Optional<ClauseEntity> read(UUID uuid) throws ServiceException;
    /**
     * Retrieves the latest version of each clause. Excluding drafts.
     */
    List<ClauseEntity> readLatestVersions() throws ServiceException;
    List<ClauseEntity> readAllDrafts() throws ServiceException;

    List<ClauseEntity> readHistory(String name);

    /**
     * Updates the status of the draft clause with the given UUID.
     * @param uuid   The UUID of the clause to update.
     * @param status The new status to set.
     * @throws NotFoundException if the clause cannot be found or if it is not in DRAFT status.
     */
    void updateDraftStatus(UUID uuid, Clause.Status status) throws NotFoundException;
}
