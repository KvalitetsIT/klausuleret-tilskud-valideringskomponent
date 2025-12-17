package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.exceptions.BadRequestException;
import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManagementServiceImpl implements ManagementService {

    private final ClauseRepositoryAdaptor repository;

    public ManagementServiceImpl(ClauseRepositoryAdaptor repository) {
        this.repository = repository;
    }

    @Override
    public Clause create(ClauseInput clause) throws ServiceException {
        if (repository.nameExists(clause.name())) {
            throw new BadRequestException("Clause with name '" + clause.name() + "' already exists");
        }
        return repository.create(clause);
    }

    @Override
    public Clause update(ClauseInput clause) throws ServiceException {
        if (!repository.nameExists(clause.name())) {
            throw new NotFoundException("Clause with name '" + clause.name() + "' not found");
        }
        return repository.create(clause);
    }

    @Override
    public Optional<Clause> read(UUID id) throws ServiceException {
        return repository.read(id);
    }

    @Override
    public List<Clause> readAll() throws ServiceException {
        return repository.readAll();
    }

    @Override
    public List<Clause> readHistory(String name) throws ServiceException {
        List<Clause> history = repository.readHistory(name);
        if (history.isEmpty())
            throw new NotFoundException(String.format("clause with name '%s' could was not found", name));
        return history;
    }
}
