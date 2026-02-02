package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity create(String name, ExpressionEntity expression, String errorMessage, Clause.Status status, Date validFrom) throws ServiceException;
    Optional<ClauseEntity> readLatestVersion(UUID uuid) throws ServiceException;
    /**
     * Retrieves the latest version of a clause. Excluding drafts.
     */
    Optional<ClauseEntity> readLatestVersion(String name) throws ServiceException;
    /**
     * Retrieves the latest version of each clause. Excluding drafts.
     */
    List<ClauseEntity> readLatestVersions() throws ServiceException;
    List<ClauseEntity> readAllDrafts() throws ServiceException;

    List<ClauseEntity> readHistory(String name);

    /**
     * Updates the clause with the given ID from DRAFT status to ACTIVE status.
     * @throws NotFoundException if the clause cannot be found or if it is not in DRAFT status.
     */
    void updateDraftToActive(UUID uuid) throws NotFoundException;
}
