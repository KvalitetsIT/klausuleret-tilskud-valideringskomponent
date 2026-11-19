package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;

import java.util.Optional;

public interface ExpressionRepository {
    ExpressionEntity create(ExpressionEntity expression);
    Optional<ExpressionEntity> read(Long id);
    void delete(Long id) throws NotFoundException;
}
