package dk.kvalitetsit.itukt.common.repository.core;

/**
 * Marks an entity that has a business identifier.
 */
interface WithUuid {
    java.util.UUID uuid();
}
