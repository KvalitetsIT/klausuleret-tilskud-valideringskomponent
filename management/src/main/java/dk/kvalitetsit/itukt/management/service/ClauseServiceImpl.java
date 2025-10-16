package dk.kvalitetsit.itukt.management.service;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.service.ClauseService;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.cache.ClauseCache;

import java.util.Optional;

public class ClauseServiceImpl implements ClauseService {


    private final ClauseCache cache;
    private final Mapper<ClauseEntity.Persisted, Clause.Persisted> mapper;

    public ClauseServiceImpl(ClauseCache cache,  Mapper<ClauseEntity.Persisted, Clause.Persisted> mapper) {
        this.cache = cache;
        this.mapper = mapper;
    }

    @Override
    public Optional<Clause.Persisted> get(String name) {
        return cache.get(name).map(mapper::map);
    }

    @Override
    public Optional<Clause.Persisted> getByErrorCode(Integer errorCode) {
        return cache.getByErrorCode(errorCode).map(mapper::map);
    }
}
