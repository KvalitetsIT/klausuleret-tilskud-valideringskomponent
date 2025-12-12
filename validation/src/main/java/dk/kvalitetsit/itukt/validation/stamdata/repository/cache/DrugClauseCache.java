package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.validation.stamdata.service.model.DrugClause;

import java.util.Optional;

public interface DrugClauseCache {
    Optional<DrugClause> get(Long drugId);
}
