package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.model.Pagination;
import dk.kvalitetsit.klaus.repository.ClauseDaoAdaptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagementServiceImpl implements ManagementService<Clause> {

    private final ClauseDaoAdaptor repository;

    public ManagementServiceImpl(ClauseDaoAdaptor repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Clause> create(Clause entry) throws ServiceException {
        return repository.create(entry);
    }

    @Override
    public List<Clause> create(List<Clause> entry) throws ServiceException {
        return repository.create(entry);
    }

    @Override
    public Optional<Clause> delete(UUID entry) throws ServiceException {
        return repository.delete(entry);
    }

    @Override
    public Optional<Clause> read(UUID id) throws ServiceException {
        return repository.read(id);
    }

    @Override
    public List<Clause> read_all(Pagination pagination) throws ServiceException {
        return repository.read_all(pagination);
    }

    @Override
    public List<Clause> read_all() throws ServiceException {
        return repository.read_all();
    }

    @Override
    public Optional<Clause> update(UUID id, Clause entry) throws ServiceException {
        return repository.update(id, entry);
    }
}
