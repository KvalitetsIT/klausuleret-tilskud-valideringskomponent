package dk.kvalitetsit.itukt.validation.repository;

import java.util.Map;
import java.util.Optional;

public class StamDataCacheImpl implements StamDataCache {
    private final Map<Long, String> drugIdToClauseNameMap;

    public StamDataCacheImpl(Map<Long, String> drugIdToClauseNameMap) {
        this.drugIdToClauseNameMap = drugIdToClauseNameMap;
    }

    @Override
    public Optional<String> getClauseNameByDrugId(long drugId) {
        return Optional.ofNullable(drugIdToClauseNameMap.get(drugId));
    }
}
