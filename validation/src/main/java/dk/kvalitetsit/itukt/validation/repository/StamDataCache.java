package dk.kvalitetsit.itukt.validation.repository;

import java.util.Optional;

public interface StamDataCache {
    Optional<String> getClauseNameByDrugId(long drugId);
}
