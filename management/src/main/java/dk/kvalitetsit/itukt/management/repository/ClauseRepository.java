package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity create(ClauseInput clause) throws ServiceException;
    Optional<ClauseEntity> read(UUID id) throws ServiceException;
    boolean nameExists(String name) throws ServiceException;
    /**
     * Retrieves all clauses, including only the latest version of each clause.
     */
    List<ClauseEntity> readAll() throws ServiceException;
}
