package dk.kvalitetsit.itukt.common.entity;


import dk.kvalitetsit.itukt.common.model.Clause;

public sealed interface ClauseEntity extends State<Clause> permits ClauseEntity.New, ClauseEntity.Persisted {
    String name();

    ExpressionEntity expression();

    // --- New Clause (not persisted yet) ---
    record New() implements ClauseEntity {

        @Override
        public String name() {
            return "";
        }

        @Override
        public ExpressionEntity expression() {
            return null;
        }
    }

    // --- Persisted Clause (has both DB id and business UUID) ---
    record Persisted(
            Long id,
            java.util.UUID uuid,
            String name,
            ExpressionEntity expression

    ) implements ClauseEntity, WithUuid { }
}


// --- Traits ---

/**
 * Marks an entity that has been persisted (DB identity).
 */
interface WithId {
    Long id();
}

/**
 * Marks an entity that has a business identifier.
 */
interface WithUuid {
    java.util.UUID uuid();
}

