package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity create(ClauseEntityInput clauseInput) throws ServiceException;
    Optional<ClauseEntity> read(UUID uuid) throws ServiceException;
    /**
     * Retrieves the current version of a clause. Excluding drafts.
     */
    Optional<ClauseEntity> readCurrentClause(String name) throws ServiceException;
    /**
     * Retrieves the current version of each clause. Excluding drafts.
     */
    List<ClauseEntity> readCurrentClauses() throws ServiceException;
    List<ClauseEntity> readAllDrafts() throws ServiceException;

    List<ClauseEntity> readHistory(String name);

    /**
     * Updates the clause with the given ID from DRAFT status to ACTIVE status.
     * @throws NotFoundException if the clause cannot be found or if it is not in DRAFT status.
     */
    ClauseEntity updateDraftToActive(UUID uuid) throws NotFoundException;

    /**
     * @param id the id associated with the clause which is to be deleted
     * @return The deleted clause
     * @throws NotFoundException if the provided id does not match any known clauses
     * @throws ServiceException if the deletion of the clause failed due to any other reason
     */
    ClauseEntity delete(UUID id) throws NotFoundException, ServiceException;
}
