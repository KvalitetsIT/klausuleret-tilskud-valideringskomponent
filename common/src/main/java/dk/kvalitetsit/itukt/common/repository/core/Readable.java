package dk.kvalitetsit.itukt.common.repository.core;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Readable<T extends InternalState<T>, PERSISTED extends InternalState.Persisted<T>, ID> {
    /**
     * Reads a persisted entity by its UUID.
     */
    Optional<PERSISTED> read(ID id) throws ServiceException;

    default List<PERSISTED> read(List<ID> ids) throws ServiceException {
        return ids.stream().map(this::read).filter(Optional::isPresent).map(Optional::get).toList();
    }

    // TODO: Move this into ClauseRepository as expressionRepository does not need it at the moment
    /**
     * Reads all persisted entities.
     */
    List<PERSISTED> readAll() throws ServiceException;

}
