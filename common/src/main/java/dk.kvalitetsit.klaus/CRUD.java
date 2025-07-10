package dk.kvalitetsit.klaus;

import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Pagination;


import java.util.List;
import java.util.UUID;

public interface CRUD<Resource> {
    Resource create(Resource entry) throws ServiceException;
    List<Resource> create(List<Resource> entry) throws ServiceException;
    Resource delete(UUID entry)  throws ServiceException;
    Resource read(UUID id) throws ServiceException;
    List<Resource> read_all(Pagination pagination) throws ServiceException;
    List<Resource> read_all() throws ServiceException;
    Resource update(UUID id, Resource entry) throws ServiceException;
}
