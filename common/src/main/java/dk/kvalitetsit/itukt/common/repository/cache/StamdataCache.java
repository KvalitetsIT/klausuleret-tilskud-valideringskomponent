package dk.kvalitetsit.itukt.common.repository.cache;

import dk.kvalitetsit.itukt.common.model.StamData;

import java.util.Optional;

public interface StamdataCache extends CacheLoader {
    Optional<StamData> get(Long drugId);
}
