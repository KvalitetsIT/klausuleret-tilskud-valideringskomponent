package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.model.Pagination;
import dk.kvalitetsit.klaus.repository.ClauseDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ManagementServiceImpl implements ManagementService<Expression> {

    private final ClauseDao repository;

    public ManagementServiceImpl(ClauseDao repository) {
        this.repository = repository;
    }

    @Override
    public Expression create(Expression entry) throws ServiceException {
        return repository.create(entry);
    }

    @Override
    public List<Expression> create(List<Expression> entry) throws ServiceException {
        return repository.create(entry);
    }

    @Override
    public Expression delete(UUID entry) throws ServiceException {
        return repository.delete(entry);
    }

    @Override
    public Expression read(UUID id) throws ServiceException {
        return repository.read(id);
    }

    @Override
    public List<Expression> read_all(Pagination pagination) throws ServiceException {
        return repository.read_all(pagination);
    }

    @Override
    public List<Expression> read_all() throws ServiceException {
        return repository.read_all();
    }

    @Override
    public Expression update(UUID id, Expression entry) throws ServiceException {
        return repository.update(id, entry);
    }
}
