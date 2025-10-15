package dk.kvalitetsit.itukt.common.repository.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Deletable<
        T extends InternalState<T>,
        PERSISTED extends InternalState.Persisted<T>
        > {
    Optional<PERSISTED> delete(UUID id);

    default List<PERSISTED> delete(List<UUID> ids) {
        return ids.stream().map(this::delete).filter(Optional::isPresent).map(Optional::get).toList();
    }
}
