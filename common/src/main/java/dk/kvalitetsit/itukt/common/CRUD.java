package dk.kvalitetsit.itukt.common;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CRUD<Resource> {
    Optional<Resource> create(Resource entry) throws ServiceException;
    Optional<Resource> read(UUID id) throws ServiceException;
    List<Resource> readAll() throws ServiceException;
}
