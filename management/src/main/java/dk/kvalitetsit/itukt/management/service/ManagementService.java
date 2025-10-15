package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagementService {
    Clause.Persisted create(Clause.NotPersisted clause) throws ServiceException;
    Optional<Clause.Persisted> read(UUID id) throws ServiceException;
    List<Clause.Persisted> readAll() throws ServiceException;
}
