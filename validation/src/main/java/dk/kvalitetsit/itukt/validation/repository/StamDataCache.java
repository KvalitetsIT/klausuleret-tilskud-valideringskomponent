package dk.kvalitetsit.itukt.validation.repository;

import java.util.Map;
import java.util.Optional;

public class StamDataCache {
    private final Map<Long, String> drugIdToClauseCodeMap;

    public StamDataCache(Map<Long, String> drugIdToClauseCodeMap) {
        this.drugIdToClauseCodeMap = drugIdToClauseCodeMap;
    }

    public Optional<String> getClauseCodeByDrugId(long drugId) {
        return Optional.ofNullable(drugIdToClauseCodeMap.get(drugId));
    }
}
