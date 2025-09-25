package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.entity.ClauseEntity;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManagementServiceImpl implements ManagementService<Clause> {

    private final ClauseRepositoryAdaptor repository;

    public ManagementServiceImpl(ClauseRepositoryAdaptor repository) {
        this.repository = repository;
    }


    @Override
    public ClauseEntity.PersistedClause create(ClauseEntity.NewClause entry) throws ServiceException {
        return repository.create(entry);
    }

    @Override
    public Optional<ClauseEntity.PersistedClause> read(UUID id) throws ServiceException {
        return repository.read(id);
    }

    @Override
    public List<ClauseEntity.PersistedClause> readAll() throws ServiceException {
            return repository.readAll();
    }
}
