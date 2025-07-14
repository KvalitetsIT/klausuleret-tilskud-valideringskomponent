package dk.kvalitetsit.klaus;

import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Pagination;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CRUD<Resource> {
    Optional<Resource> create(Resource entry) throws ServiceException;
    List<Resource> create(List<Resource> entry) throws ServiceException;
    Optional<Resource> delete(UUID entry)  throws ServiceException;
    Optional<Resource> read(UUID id) throws ServiceException;
    List<Resource> read_all(Pagination pagination) throws ServiceException;
    List<Resource> read_all() throws ServiceException;
    Optional<Resource> update(UUID id, Resource entry) throws ServiceException;
}
