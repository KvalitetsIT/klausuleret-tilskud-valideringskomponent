package dk.kvalitetsit.itukt.validation.repository.cache;

import dk.kvalitetsit.itukt.validation.service.model.DrugClause;

import java.util.Optional;

public interface DrugClauseCache {
    Optional<DrugClause> get(Long drugId);
}
