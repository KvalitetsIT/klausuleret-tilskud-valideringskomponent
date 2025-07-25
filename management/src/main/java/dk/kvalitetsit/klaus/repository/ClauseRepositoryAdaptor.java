package dk.kvalitetsit.klaus.repository;

import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.*;
import dk.kvalitetsit.klaus.repository.model.ClauseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClauseRepositoryAdaptor implements ClauseRepository<Clause> {

    private final ClauseRepository<ClauseEntity> validationDao;
    private final Mapper<Clause, ClauseEntity> modelMapper;
    private final Mapper<ClauseEntity, Clause> entityMapper;

    public ClauseRepositoryAdaptor(ClauseRepository<ClauseEntity> validationDao, Mapper<Clause, ClauseEntity> modelMapper, Mapper<ClauseEntity, Clause> entityMapper) {
        this.validationDao = validationDao;
        this.modelMapper = modelMapper;
        this.entityMapper = entityMapper;
    }

    public Optional<Clause> create(Clause entry) throws ServiceException {
        return validationDao.create(modelMapper.map(entry)).map(entityMapper::map);
    }

    public List<Clause> create(List<Clause> entry) throws ServiceException {
        return validationDao.create(entry.stream().map(this.modelMapper::map).toList()).stream().map(this.entityMapper::map).toList();
    }

    public Optional<Clause> delete(UUID id) throws ServiceException {
        return validationDao.delete(id).map(entityMapper::map);
    }

    public Optional<Clause> read(UUID id) throws ServiceException {
        return validationDao.read(id).map(entityMapper::map);
    }

    public List<Clause> read_all(Pagination pagination) throws ServiceException {
        return validationDao.read_all(pagination).stream().map(entityMapper::map).toList();
    }

    public List<Clause> read_all() throws ServiceException {
        return validationDao.read_all().stream().map(this.entityMapper::map).toList();
    }

    public Optional<Clause> update(UUID id, Clause entry) throws ServiceException {
        return validationDao.update(id, modelMapper.map(entry)).map(entityMapper::map);
    }
}
