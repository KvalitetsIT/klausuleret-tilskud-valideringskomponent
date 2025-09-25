package dk.kvalitetsit.itukt.common.entity;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CRUD<N extends State<T>, P extends State<T>, T> {

    /**
     * Creates a new entity and returns the persisted version.
     */
    P create(N entry) throws ServiceException;

    /**
     * Reads a persisted entity by its UUID.
     */
    Optional<P> read(UUID id) throws ServiceException;

    /**
     * Reads all persisted entities.
     */
    List<P> readAll() throws ServiceException;
}
