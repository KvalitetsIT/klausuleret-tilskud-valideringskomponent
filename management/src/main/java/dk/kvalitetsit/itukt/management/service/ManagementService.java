package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagementService {
    Clause create(Clause entry) throws ServiceException;
    Optional<Clause> read(UUID id) throws ServiceException;
    List<Clause> readAll() throws ServiceException;
}
