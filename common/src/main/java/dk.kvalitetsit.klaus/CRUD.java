package dk.kvalitetsit.klaus;

import dk.kvalitetsit.klaus.exceptions.ServiceException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CRUD<Resource> {
    Optional<Resource> create(Resource entry) throws ServiceException;
    Optional<Resource> read(UUID id) throws ServiceException;
    List<Resource> readAll() throws ServiceException;
}
