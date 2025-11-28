package dk.kvalitetsit.itukt.validation.repository.cache;

import java.util.Optional;

public interface Cache<T> {
    Optional<T> get(Long drugId);
}
