package dk.kvalitetsit.itukt.validation.repository;

import java.util.Map;
import java.util.Optional;

public class StamDataCache {
    private final Map<Long, String> drugIdToClauseNameMap;

    public StamDataCache(Map<Long, String> drugIdToClauseNameMap) {
        this.drugIdToClauseNameMap = drugIdToClauseNameMap;
    }

    public Optional<String> getClauseNameByDrugId(long drugId) {
        return Optional.ofNullable(drugIdToClauseNameMap.get(drugId));
    }
}
