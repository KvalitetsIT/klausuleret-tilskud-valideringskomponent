package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import java.util.Optional;

public interface Cache<T, R> {

    Optional<R> get(T code);
}
