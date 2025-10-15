package dk.kvalitetsit.itukt.common.repository.core;


/**
 * The interface is supposed to map the two state which an entity has. Whether it is persisted and therefore is constructed from the values given by the database or the notPersisted which only include the essentials
 *
 * @param <T> the essential model
 */
sealed interface InternalState<T> permits State, InternalState.NotPersisted, InternalState.Persisted {

    /**
     * Marker interface for new state
     */
    non-sealed interface NotPersisted<T> extends InternalState<T> {
    }

    /**
     * Marker interface for persisted state
     */
    non-sealed interface Persisted<T> extends WithId, InternalState<T> {
    }

}

