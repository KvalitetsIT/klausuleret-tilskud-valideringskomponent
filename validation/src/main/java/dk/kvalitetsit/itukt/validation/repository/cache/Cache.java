package dk.kvalitetsit.itukt.validation.repository.cache;

import java.util.Optional;

public interface Cache<T, R> {

    Optional<R> get(T code);
}
