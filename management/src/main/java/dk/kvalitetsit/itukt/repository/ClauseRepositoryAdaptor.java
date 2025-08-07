package dk.kvalitetsit.itukt.repository;

import dk.kvalitetsit.itukt.Mapper;
import dk.kvalitetsit.itukt.exceptions.ServiceException;
import dk.kvalitetsit.itukt.model.*;
import dk.kvalitetsit.itukt.repository.model.ClauseEntity;

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

    public Optional<Clause> read(UUID id) throws ServiceException {
        return validationDao.read(id).map(entityMapper::map);
    }

    public List<Clause> readAll() throws ServiceException {
        return validationDao.readAll().stream().map(this.entityMapper::map).toList();
    }
}
