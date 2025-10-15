package dk.kvalitetsit.itukt.common.repository.core;

/**
 * Marks an entity that has been persisted (DB identity).
 */
interface WithId {
    Long id();
}
