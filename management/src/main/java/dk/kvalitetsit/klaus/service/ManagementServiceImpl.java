package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.repository.ClauseRepositoryAdaptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagementServiceImpl implements ManagementService<Clause> {

    private final ClauseRepositoryAdaptor repository;

    public ManagementServiceImpl(ClauseRepositoryAdaptor repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Clause> create(Clause entry) throws ServiceException {
        return repository.create(entry);
    }

    @Override
    public Optional<Clause> read(UUID id) throws ServiceException {
        return repository.read(id);
    }

    @Override
    public List<Clause> read_all() throws ServiceException {
        return repository.read_all();
    }
}
