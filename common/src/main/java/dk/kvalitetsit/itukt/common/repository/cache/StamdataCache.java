package dk.kvalitetsit.itukt.common.repository.cache;

import dk.kvalitetsit.itukt.common.model.StamdataEntity;

import java.util.Optional;

public interface StamdataCache extends CacheLoader {
    Optional<StamdataEntity> get(Long name);
}
