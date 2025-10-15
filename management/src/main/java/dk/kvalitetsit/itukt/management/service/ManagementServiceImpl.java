package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManagementServiceImpl implements ManagementService {

    private final ClauseRepositoryAdaptor repository;

    public ManagementServiceImpl(ClauseRepositoryAdaptor repository) {
        this.repository = repository;
    }

    @Override
    public Clause.Persisted create(Clause.NotPersisted clause) throws ServiceException {
        return repository.create(clause);
    }

    @Override
    public Optional<Clause.Persisted> read(UUID id) throws ServiceException {
        return repository.read(id);
    }

    @Override
    public List<Clause.Persisted> readAll() throws ServiceException {
        return repository.readAll();
    }
}
