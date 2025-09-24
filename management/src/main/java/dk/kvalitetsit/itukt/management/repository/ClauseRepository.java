package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.service.model.ClauseForCreation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity create(ClauseForCreation clause) throws ServiceException;
    Optional<ClauseEntity> read(UUID id) throws ServiceException;
    List<ClauseEntity> readAll() throws ServiceException;
}
