package dk.kvalitetsit.itukt.common.entity;

public sealed interface State<T> permits ClauseEntity, ClauseModel, State.New, State.Persisted {

    /** Marker interface for new state */
    non-sealed interface New<T> extends State<T> {
    }

    /** Marker interface for persisted state */
    non-sealed interface Persisted<T> extends WithId, State<T> { }
}
