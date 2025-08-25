package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.validation.repository.entity.ClauseEntity;

import java.util.Map;
import java.util.Optional;

public class StamDataCache {
    private final Map<Long, ClauseEntity> drugIdToClauseMap;

    public StamDataCache(Map<Long, ClauseEntity> drugIdToClauseMap) {
        this.drugIdToClauseMap = drugIdToClauseMap;
    }

    public Optional<ClauseEntity> getClauseByDrugId(long drugId) {
        return Optional.ofNullable(drugIdToClauseMap.get(drugId));
    }
}
