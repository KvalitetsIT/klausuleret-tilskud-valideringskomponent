package dk.kvalitetsit.itukt.management.repository;

import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntityInput;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClauseRepository {
    ClauseEntity create(ClauseEntityInput clauseInput);
    Optional<ClauseEntity> read(UUID uuid);
    List<ClauseEntity> read(ClauseQuery query);
    /**
     * Retrieves the current version of a clause. Excluding drafts.
     */
    Optional<ClauseEntity> readCurrentNonDraftClause(String name);
    /**
     * Retrieves the draft clause with no child clauses, if such exist.
     */
    Optional<ClauseEntity> readCurrentDraft(String name);
    /**
     * Retrieves the current version of each clause. Excluding drafts.
     */
    List<ClauseEntity> readCurrentNonDraftClauses();
    /**
     * Retrieves all draft clauses that have no child clauses.
     */
    List<ClauseEntity> readCurrentDrafts();

    List<ClauseEntity> readCurrent(String name, Clause.Status ... statuses);

    List<ClauseEntity> readHistory(String name);

    /**
     * @param id the id associated with the clause which is to be deleted
     * @return The deleted clause
     * @throws NotFoundException if the provided id does not match any known clauses
     */
    ClauseEntity deleteDraft(UUID id) throws NotFoundException;

    Optional<ClauseEntity> readParent(UUID uuid);
}
