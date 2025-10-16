package dk.kvalitetsit.itukt.common.repository.core;

import java.util.Optional;
import java.util.UUID;

public interface Updatable<
        T extends InternalState<T>,
        PERSISTED extends InternalState.Persisted<T>
        > {

    Optional<PERSISTED> update(UUID id, PERSISTED updatedEntry);

}
