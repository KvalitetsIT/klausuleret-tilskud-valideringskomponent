package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

import java.util.Optional;

public interface ExpressionRepository {
    ExpressionEntity create(ExpressionEntity expression) throws ServiceException;
    Optional<ExpressionEntity> read(Long id) throws ServiceException;
}
