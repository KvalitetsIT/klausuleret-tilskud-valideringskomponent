package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.validation.service.model.StamData;

import java.util.Optional;

public interface StamdataCache {
    Optional<StamData> get(Long drugId);
}
