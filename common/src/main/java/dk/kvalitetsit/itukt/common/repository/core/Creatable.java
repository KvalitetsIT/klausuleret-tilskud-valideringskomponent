package dk.kvalitetsit.itukt.common.repository.core;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;

import java.util.List;

public interface Creatable<
        T extends InternalState<T>,
        PERSISTED extends InternalState.Persisted<T>,
        NOT_PERSISTED extends InternalState.NotPersisted<T>
        > {
    /**
     * Creates a new entity and returns the persisted version.
     */
    PERSISTED create(NOT_PERSISTED entry) throws ServiceException;

    default List<PERSISTED> create(List<NOT_PERSISTED> entry) throws ServiceException {
        return entry.stream().map(this::create).toList();
    }
}
