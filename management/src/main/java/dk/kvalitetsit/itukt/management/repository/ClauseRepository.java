package dk.kvalitetsit.itukt.management.repository;

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
     * @param id the id associated with the clause which is to be deleted
     * @return The deleted clause
     * @throws NotFoundException if the provided id does not match any known clauses
     */
    ClauseEntity deleteDraft(UUID id) throws NotFoundException;

    Optional<ClauseEntity> readParent(UUID uuid);
}
