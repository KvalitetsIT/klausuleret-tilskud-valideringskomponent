package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity create(String name, ExpressionEntity expression) throws ServiceException;
    Optional<ClauseEntity> read(UUID id) throws ServiceException;
    List<ClauseEntity> readAll() throws ServiceException;
}
