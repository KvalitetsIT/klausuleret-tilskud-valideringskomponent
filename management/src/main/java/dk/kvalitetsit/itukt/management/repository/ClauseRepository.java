package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity create(ClauseEntityInput clauseInput);
    Optional<ClauseEntity> read(UUID uuid);
    /**
     * Retrieves the current version of a clause. Excluding drafts.
     */
    Optional<ClauseEntity> readCurrentClause(String name);
    /**
     * Retrieves the current version of each clause. Excluding drafts.
     */
    List<ClauseEntity> readCurrentClauses();
    List<ClauseEntity> readAllDrafts();

    List<ClauseEntity> readHistory(String name);

    /**
     * @param id the id associated with the clause which is to be deleted
     * @return The deleted clause
     * @throws NotFoundException if the provided id does not match any known clauses
     */
    ClauseEntity deleteDraft(UUID id) throws NotFoundException;
}
