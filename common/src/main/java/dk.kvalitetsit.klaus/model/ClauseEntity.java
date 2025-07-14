package dk.kvalitetsit.klaus.model;

import java.util.UUID;

public record ClauseEntity(Long id, UUID uuid, String name, ExpressionEntity expression) {
    static ClauseEntity from(Number primaryKey, UUID uuid, ClauseEntity clause) {
        return new ClauseEntity(primaryKey.longValue(), uuid, clause.name(), clause.expression());
    }


}
